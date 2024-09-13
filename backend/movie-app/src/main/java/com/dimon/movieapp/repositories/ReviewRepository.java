package com.dimon.movieapp.repositories;

import com.dimon.movieapp.models.FilmEntity;
import com.dimon.movieapp.models.LocalUser;
import com.dimon.movieapp.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByFilm(FilmEntity film);
    List<Review> findByUser(LocalUser user);
}
