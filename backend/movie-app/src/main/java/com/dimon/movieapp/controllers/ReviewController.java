package com.dimon.movieapp.controllers;

import com.dimon.movieapp.dto.ReviewDto;
import com.dimon.movieapp.exceptions.ResourceNotFoundException;
import com.dimon.movieapp.models.LocalUser;
import com.dimon.movieapp.models.Review;
import com.dimon.movieapp.services.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // TODO: HERE I NEED ADD EXCEPTION ABOUT REVIEW, IF REVIEW BODY IS NULL
    @PostMapping("/add")
    public ResponseEntity<Review> addReview(@AuthenticationPrincipal LocalUser user, @RequestBody ReviewDto review) {
        Review review1 = reviewService.addReview(user.getId(), review.getFilmId(), review.getReview());
        return new ResponseEntity<>(review1, HttpStatus.CREATED);
    }

    @GetMapping("/{filmId}")
    public ResponseEntity<?> getAllReviewByFilmId(@PathVariable("filmId") Long filmId) {
        System.out.println("Get all reviews");
        try {
            List<Review> reviews = reviewService.getAllReviewByFilmId(filmId);
            return ResponseEntity.ok(reviews);
        } catch (ResourceNotFoundException ex) {
            // Return 404 only if the film is not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
