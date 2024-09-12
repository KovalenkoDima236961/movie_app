package com.dimon.movieapp.controllers;

import com.dimon.movieapp.dto.FilmDto;
import com.dimon.movieapp.exceptions.FilmAlreadyInWatchlistException;
import com.dimon.movieapp.models.FilmEntity;
import com.dimon.movieapp.models.LocalUser;
import com.dimon.movieapp.services.FilmService;
import com.dimon.movieapp.utils.Convertor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class WatchlistController {

    private final FilmService filmService;
    private final Convertor convertor;

    @Autowired
    public WatchlistController(FilmService filmService, Convertor convertor) {
        this.filmService = filmService;
        this.convertor = convertor;
    }

    @PostMapping("/addwatchlist")
    public ResponseEntity<?> addToWatchlist(@RequestBody FilmDto filmDto, @AuthenticationPrincipal LocalUser user) {
        System.out.println("I am adding to watchlist");
        try {
            filmService.addFilmToWatchlist(filmDto, user);
            return ResponseEntity.ok("The movie has been successfully added to the watchlist.");
        } catch (FilmAlreadyInWatchlistException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @GetMapping("/inwatchlist/{filmId}")
    public ResponseEntity<?> checkIfFilmInWatchlist(@PathVariable Long filmId, @AuthenticationPrincipal LocalUser user) {
        System.out.println("Check if film in watchlist");
        boolean isInWatchlist = user.getWatchlist().stream()
                .anyMatch(film -> film.getFilmId().equals(filmId));

        if(isInWatchlist) {
            return ResponseEntity.ok("IN");
        } else {
            return ResponseEntity.ok("NO");
        }
    }

    @GetMapping("/watchlist")
    public ResponseEntity<?> getWatchlist(@AuthenticationPrincipal LocalUser user) {
        System.out.println("I get watchlist");
        Set<FilmEntity> watchlist = user.getWatchlist();

        List<FilmDto> watchlistDto = watchlist.stream()
                .map(film -> convertor.convertToFilmDto(film))
                .collect(Collectors.toList());

        watchlistDto.forEach(System.out::println);

        return ResponseEntity.ok(watchlistDto);
    }

    @DeleteMapping("/removewatchlist/{filmId}")
    public ResponseEntity<?> removeFromWatchlist(@PathVariable Long filmId,
                                                 @AuthenticationPrincipal LocalUser user) {
        filmService.removeFilmFromWatchlist(filmId, user);

        return ResponseEntity.ok("The movie has been successfully removed from the watchlist");
    }

}
