package it.unical.ea_project.service.impl;

import it.unical.ea_project.domain.Review;
import it.unical.ea_project.dto.ReviewDTO;
import it.unical.ea_project.mapper.ReviewMapper;
import it.unical.ea_project.repository.ActivityRepository;
import it.unical.ea_project.repository.ReviewRepository;
import it.unical.ea_project.repository.TripRepository;
import it.unical.ea_project.repository.UserRepository;
import it.unical.ea_project.service.ReviewService;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final ActivityRepository activityRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository, TripRepository tripRepository, ActivityRepository activityRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
        this.activityRepository = activityRepository;
    }

    @Transactional
    public void create(ReviewDTO review) {
        // TO ADD VALIDATION

        Review reviewEntity = new Review();
        reviewEntity.setUser(userRepository.getReferenceById(review.getUserId()));

        if(review.getTripId() != null){
            reviewEntity.setTrip(tripRepository.getReferenceById(review.getTripId()));
        }
        if(review.getActivityId() != null){
            reviewEntity.setActivity(activityRepository.getReferenceById(review.getActivityId()));
        }

        reviewEntity.setRating(review.getRating());
        reviewEntity.setComment(review.getComment());

        reviewRepository.save(reviewEntity);
    }

    @Transactional(readOnly = true)
    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review non trovata"));
    }

    @Transactional(readOnly = true)
    public List<Review> findByUser(Long userId) {
        return reviewRepository.findAllByUserId(userId);
    }


    @Nullable
    @Transactional(readOnly = true)
    public List<ReviewDTO> findByTrip(Long tripId) {

        List<Review> reviews = reviewRepository.findAllByTripTripId(tripId);

        return reviews.stream()
                .map(ReviewMapper::toReviewDTO)
                .collect(Collectors.toList());
    }

    @Nullable
    @Transactional(readOnly = true)
    public List<ReviewDTO> findByActivity(Long activityId) {

        List<Review> reviews = reviewRepository.findAllByActivityActivityId(activityId);

        return reviews.stream()
                .map(ReviewMapper::toReviewDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Review non trovata");
        }
        reviewRepository.softDeleteById(id);
    }
}
