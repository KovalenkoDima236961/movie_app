package com.dimon.movieapp.exceptionHandler;

import com.dimon.movieapp.exceptions.FilmNotFoundInWatchlistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FilmNotFoundInWatchlistException.class)
    public ResponseEntity<String> handleFilmNotFoundException(FilmNotFoundInWatchlistException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
}
