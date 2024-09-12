package com.dimon.movieapp.utils;

import com.dimon.movieapp.dto.FilmDto;
import com.dimon.movieapp.models.FilmEntity;
import org.springframework.stereotype.Component;

@Component
public class Convertor {


    public static FilmEntity convertToFilmEntity(FilmDto filmDto) {
        return FilmEntity.builder()
                .filmId(filmDto.getFilmId())
                .title(filmDto.getTitle())
                .type(filmDto.getType())
                .posterPath(filmDto.getPoster_path())
                .releaseDate(filmDto.getRelease_date())
                .voteAverage(filmDto.getVote_average())
                .overview(filmDto.getOverview())
                .build();
    }

    public static FilmDto convertToFilmDto(FilmEntity filmEntity) {
        return FilmDto.builder()
                .filmId(filmEntity.getFilmId())
                .title(filmEntity.getTitle())
                .type(filmEntity.getType())
                .poster_path(filmEntity.getPosterPath())
                .release_date(filmEntity.getReleaseDate())
                .vote_average(filmEntity.getVoteAverage())
                .overview(filmEntity.getOverview())
                .build();
    }
}
