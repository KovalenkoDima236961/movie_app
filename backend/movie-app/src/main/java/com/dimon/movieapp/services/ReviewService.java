package com.dimon.movieapp.services;

import com.dimon.movieapp.exceptions.ResourceNotFoundException;
import com.dimon.movieapp.models.FilmEntity;
import com.dimon.movieapp.models.LocalUser;
import com.dimon.movieapp.models.Review;
import com.dimon.movieapp.repositories.FilmRepository;
import com.dimon.movieapp.repositories.LocalUserRepository;
import com.dimon.movieapp.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private LocalUserRepository userRepository;

    public Review addReview(Long userId, Long filmId, String reviewText) {
        // Throw new exception
        LocalUser user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        FilmEntity film = filmRepository.findById(filmId)
                .orElseThrow(() -> new ResourceNotFoundException("Film not found"));

        Review review = new Review();
        review.setUser(user);
        review.setFilm(film);
        review.setReview(reviewText);
        review.setCreated_at(new Timestamp(System.currentTimeMillis()));

        return reviewRepository.save(review);
    }

    public List<Review> getAllReviewByFilmId(Long filmId) {
        FilmEntity film = filmRepository.findByFilmId(filmId)
                .orElseThrow(() -> new ResourceNotFoundException("Film not found with id: " + filmId));

        return reviewRepository.findByFilm(film);
    }
}
