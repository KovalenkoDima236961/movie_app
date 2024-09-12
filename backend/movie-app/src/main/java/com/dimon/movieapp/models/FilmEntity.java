package com.dimon.movieapp.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "films")
@Builder
@AllArgsConstructor
@Getter
@Setter
public class FilmEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "film_id")
    private Long filmId;

    @NotBlank
    @NotNull
    @Column(name = "title")
    private String title;

    @NotBlank
    @NotNull
    @Column(name = "type")
    private String type;

    @Column(name = "poster_path")
    private String posterPath;

    @Column(name = "release_date")
    private String releaseDate;

    @Column(name = "vote_average")
    private double voteAverage;

    @Column(name = "overview", columnDefinition = "TEXT")
    private String overview;

    @JsonIgnore
    @ManyToMany(mappedBy = "watchlist")
    private Set<LocalUser> users = new HashSet<>();

    public FilmEntity() {
        this.users = new HashSet<>();
    }

    public FilmEntity(Long filmId, String title, String type, String posterPath, String releaseDate, double voteAverage, String overview) {
        this.filmId = filmId;
        this.title = title;
        this.type = type;
        this.posterPath = posterPath;
        this.releaseDate = releaseDate;
        this.voteAverage = voteAverage;
        this.overview = overview;
    }

    public void addUser(LocalUser user) {
        if(users == null) {
            users = new HashSet<>();
        }
        this.users.add(user);
        user.getWatchlist().add(this);
    }

    // Helper method to remove user from the film's users set
    public void removeUser(LocalUser user) {
        this.users.remove(user);
        user.getWatchlist().remove(this);
    }
}
