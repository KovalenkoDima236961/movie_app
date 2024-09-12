package com.dimon.movieapp.exceptions;

public class FilmNotFoundInWatchlistException extends RuntimeException {
    public FilmNotFoundInWatchlistException(String message) {
        super(message);
    }

    public FilmNotFoundInWatchlistException(String message, Throwable cause) {
        super(message, cause);
    }
}
