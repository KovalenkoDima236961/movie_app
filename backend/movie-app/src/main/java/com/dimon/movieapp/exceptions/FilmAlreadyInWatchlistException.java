package com.dimon.movieapp.exceptions;

public class FilmAlreadyInWatchlistException extends RuntimeException{

    public FilmAlreadyInWatchlistException(String message) {
        super(message);
    }
}

