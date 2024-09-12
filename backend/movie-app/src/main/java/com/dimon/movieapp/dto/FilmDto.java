package com.dimon.movieapp.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FilmDto {
    @NotNull
    private Long filmId;
    @NotNull
    @NotBlank
    private String title;

    // add validation that type can be only tv or movie
    @NotNull
    @NotBlank
    private String type;
    @NotNull
    @NotBlank
    private String poster_path;
    @NotNull
    @NotBlank
    private String release_date;
    @NotNull
    private double vote_average;
    @NotNull
    @NotBlank
    private String overview;
}
