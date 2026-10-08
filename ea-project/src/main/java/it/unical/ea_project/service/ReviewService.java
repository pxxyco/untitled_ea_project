package it.unical.ea_project.service;

import it.unical.ea_project.domain.Review;
import it.unical.ea_project.dto.ReviewDTO;
import org.jspecify.annotations.Nullable;

import java.util.List;

public interface ReviewService {

    ReviewDTO create(ReviewDTO review);

    Review findById(Long id);

    List<Review> findByUser(Long userId);

    @Nullable List<ReviewDTO> findByActivity(Long activityId);

    @Nullable List<ReviewDTO> findByTrip(Long tripId);

    void delete(Long id);

}
