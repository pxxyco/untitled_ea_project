package it.unical.ea_project.controller;

import it.unical.ea_project.dto.ReviewDTO;
import it.unical.ea_project.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(@RequestBody ReviewDTO review){
        // TODO: add data validation

        // TODO: use authentication for user id
        // review.setUserId(userId);

        // TEST
        // review.setUserId("1");

        return ResponseEntity.ok(reviewService.create(review));
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(reviewService.findByTrip(tripId));
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByActivity(@PathVariable Long activityId) {
        return ResponseEntity.ok(reviewService.findByActivity(activityId));
    }


}
