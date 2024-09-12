package com.dimon.movieapp.repositories;

import com.dimon.movieapp.models.FilmEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilmRepository extends JpaRepository<FilmEntity, Long> {
    Optional<FilmEntity> findByFilmId(Long filmId);

}
