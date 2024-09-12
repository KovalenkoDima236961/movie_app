package com.dimon.movieapp.services;

import com.dimon.movieapp.dto.FilmDto;
import com.dimon.movieapp.exceptions.FilmAlreadyInWatchlistException;
import com.dimon.movieapp.exceptions.FilmNotFoundInWatchlistException;
import com.dimon.movieapp.models.FilmEntity;
import com.dimon.movieapp.models.LocalUser;
import com.dimon.movieapp.repositories.FilmRepository;
import com.dimon.movieapp.repositories.LocalUserRepository;
import com.dimon.movieapp.utils.Convertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.FileAlreadyExistsException;
import java.util.Optional;

@Service
public class FilmService {

    private final Convertor convertor;
    private final FilmRepository filmRepository;
    private final LocalUserRepository localUserRepository;

    @Autowired
    public FilmService(Convertor convertor, FilmRepository filmRepository, LocalUserRepository localUserRepository) {
        this.convertor = convertor;
        this.filmRepository = filmRepository;
        this.localUserRepository = localUserRepository;
    }

    @Transactional
    public void addFilmToWatchlist(FilmDto filmDto, LocalUser user) {
        FilmEntity film = Convertor.convertToFilmEntity(filmDto);
        Optional<FilmEntity> existingFilm = filmRepository.findByFilmId(filmDto.getFilmId());
        FilmEntity filmToAdd = existingFilm.orElseGet(() -> filmRepository.save(film));

        System.out.println(filmToAdd);

        user.getWatchlist().forEach(filmInArray -> {
            if(filmInArray.getFilmId().equals(filmToAdd.getFilmId())) {
                throw new FilmAlreadyInWatchlistException("This movie has been added to your watchlist.");
            }
        });


        user.addFilmToWatchlist(filmToAdd);
        localUserRepository.save(user);
    }

    @Transactional
    public void removeFilmFromWatchlist(Long filmId, LocalUser user) {
        FilmEntity filmToRemove = user.getWatchlist().stream()
                .filter((film) -> film.getFilmId().equals(filmId))
                .findFirst()
                .orElseThrow(() -> new FilmNotFoundInWatchlistException("Film not found in  user's watchlist"));

        // Remove the film from both sides of the relationship
        user.removeFilmFromWatchlist(filmToRemove);
        filmToRemove.removeUser(user);// Synchronize the other side of the relationship

        // Save the changes
        localUserRepository.save(user);
        filmRepository.save(filmToRemove); // Optional, but useful in a bidirectional relationship

        localUserRepository.flush();
        filmRepository.flush();
    }

}
