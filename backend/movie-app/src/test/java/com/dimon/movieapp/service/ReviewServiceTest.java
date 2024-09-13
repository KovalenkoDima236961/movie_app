package com.dimon.movieapp.service;

import com.dimon.movieapp.exceptions.ResourceNotFoundException;
import com.dimon.movieapp.models.FilmEntity;
import com.dimon.movieapp.models.LocalUser;
import com.dimon.movieapp.models.Review;
import com.dimon.movieapp.repositories.FilmRepository;
import com.dimon.movieapp.repositories.LocalUserRepository;
import com.dimon.movieapp.repositories.ReviewRepository;
import com.dimon.movieapp.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Sql(scripts = "/data.sql")
public class ReviewServiceTest {

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private FilmRepository filmRepository;

    @Autowired
    private LocalUserRepository userRepository;

    @Test
    public void testAddReview_Success() {
        // Given
        Long userId = 1L; // Assuming UserA has ID 1
        Long filmId = 1L; // Assuming Movie 101 has ID 1
        String reviewText = "Amazing movie!";

        // When
        Review review = reviewService.addReview(userId, filmId, reviewText);

        // Then
        assertNotNull(review);
        assertEquals(reviewText, review.getReview());
        assertEquals(userId, review.getUser().getId());
        assertEquals(filmId, review.getFilm().getId());
    }

    @Test
    public void testAddReview_UserNotFound() {
        // Given
        Long userId = 999L; // Non-existent user
        Long filmId = 1L;
        String reviewText = "Amazing movie!";

        // When & Then
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.addReview(userId, filmId, reviewText);
        });

        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void testAddReview_FilmNotFound() {
        // Given
        Long userId = 1L;
        Long filmId = 999L; // Non-existent film
        String reviewText = "Amazing movie!";

        // When & Then
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.addReview(userId, filmId, reviewText);
        });

        assertEquals("Film not found", exception.getMessage());
    }

    @Test
    public void testGetAllReviewByFilmId_Success() {
        // Given
        Long filmId = 101L; // Movie 101's filmId
        FilmEntity film = filmRepository.findByFilmId(filmId).orElse(null);
        LocalUser user = userRepository.findById(1L).orElse(null);

        // Add two reviews for the film
        Review review1 = new Review();
        review1.setReview("Great action sequences.");
        review1.setFilm(film);
        review1.setUser(user);
        reviewRepository.save(review1);

        Review review2 = new Review();
        review2.setReview("Loved the storyline.");
        review2.setFilm(film);
        review2.setUser(user);
        reviewRepository.save(review2);

        // When
        List<Review> reviews = reviewService.getAllReviewByFilmId(filmId);

        // Then
        assertNotNull(reviews);
        assertEquals(2, reviews.size());
    }

    @Test
    public void testGetAllReviewByFilmId_NoReviews() {
        // Given
        Long filmId = 102L; // Movie 102's filmId, assuming it has no reviews

        // When
        List<Review> reviews = reviewService.getAllReviewByFilmId(filmId);

        // Then
        assertNotNull(reviews);
        assertTrue(reviews.isEmpty());
    }


    @Test
    public void testGetAllReviewByFilmId_FilmNotFound() {
        // Given
        Long filmId = 999L; // Non-existent film

        // When & Then
        Exception exception = assertThrows(ResourceNotFoundException.class, () -> {
            reviewService.getAllReviewByFilmId(filmId);
        });

        assertEquals("Film not found with id: 999", exception.getMessage());
    }
}
