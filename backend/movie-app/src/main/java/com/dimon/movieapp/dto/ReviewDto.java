package com.dimon.movieapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewDto {
    @NotNull
    private Long filmId;
    @NotNull
    @NotBlank
    private String review;
}
