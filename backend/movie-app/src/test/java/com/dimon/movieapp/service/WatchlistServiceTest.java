package com.dimon.movieapp.service;

import com.dimon.movieapp.dto.FilmDto;
import com.dimon.movieapp.exceptions.FilmAlreadyInWatchlistException;
import com.dimon.movieapp.models.FilmEntity;
import com.dimon.movieapp.models.LocalUser;
import com.dimon.movieapp.repositories.FilmRepository;
import com.dimon.movieapp.repositories.LocalUserRepository;
import com.dimon.movieapp.services.FilmService;
import com.dimon.movieapp.utils.Convertor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
public class WatchlistServiceTest {

    @Autowired
    private LocalUserRepository userRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private FilmService filmService;
    @Autowired
    private Convertor convertor;

    @Test
    public void testAddFilmToWatchlist() throws Exception {
        LocalUser user = userRepository.findByUsernameIgnoreCase("UserA").orElseThrow();

        FilmDto filmDto = new FilmDto();
        filmDto.setFilmId(123L);
        filmDto.setTitle("Test Movie");
        filmDto.setType("Action");
        filmDto.setPoster_path("/test/path.jpg");
        filmDto.setRelease_date("2024-01-01");
        filmDto.setVote_average(8.5);
        filmDto.setOverview("This is a test movie.");

        filmService.addFilmToWatchlist(filmDto, user);

        LocalUser updatedUser = userRepository.findByUsernameIgnoreCase("UserA").orElseThrow();
        Optional<FilmEntity> addedFilm = updatedUser.getWatchlist().stream()
                .filter(film -> film.getFilmId().equals(123L))
                .findFirst();

        assertTrue(addedFilm.isPresent(), "Film should be added to the watchlist");

        FilmEntity film = addedFilm.get();

        FilmAlreadyInWatchlistException exception = assertThrows(FilmAlreadyInWatchlistException.class, () -> {
            filmService.addFilmToWatchlist(filmDto, user);
        });

        // Assert: Verify that the correct exception message is returned
        assertEquals("This movie has been added to your watchlist.", exception.getMessage());
    }


    // TODO FIX THIS
//    @Test
//    public void testRemoveFilmFromWatchlist() throws Exception {
//        LocalUser user = userRepository.findByUsernameIgnoreCase("UserA").orElseThrow();
//        FilmDto filmDto = new FilmDto();
//        filmDto.setFilmId(124L);
//        filmDto.setTitle("Test Movie4");
//        filmDto.setType("Action4");
//        filmDto.setPoster_path("/test/path.jpg4");
//        filmDto.setRelease_date("2024-01-014");
//        filmDto.setVote_average(8.54);
//        filmDto.setOverview("This is a test movie.4");
//
//        filmService.addFilmToWatchlist(filmDto, user);
//
//        filmService.removeFilmFromWatchlist(124L, user);
//
//        LocalUser updatedUser = userRepository.findByUsernameIgnoreCase("UserA").orElseThrow();
//
//        System.out.println(updatedUser);
//        updatedUser.getWatchlist().forEach(filmEntity -> {
//            System.out.println(filmEntity.getFilmId());
//        });
//        assertFalse(updatedUser.getWatchlist().stream().anyMatch(f -> f.getFilmId().equals(124L)));
//    }

}
