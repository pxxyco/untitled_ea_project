package it.unical.ea_project.service.impl;

import it.unical.ea_project.domain.Response;
import it.unical.ea_project.domain.Review;
import it.unical.ea_project.dto.ReviewDTO;
import it.unical.ea_project.mapper.ReviewMapper;
import it.unical.ea_project.repository.*;
import it.unical.ea_project.service.ReviewService;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;
    private final ActivityRepository activityRepository;
    private final ResponseRepository responseRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             UserRepository userRepository,
                             TripRepository tripRepository,
                             ActivityRepository activityRepository,
                             ResponseRepository responseRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
        this.activityRepository = activityRepository;
        this.responseRepository = responseRepository;
    }

    @Transactional
    public ReviewDTO create(ReviewDTO review) {
        // TODO: add data validation

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

        Review saved = reviewRepository.save(reviewEntity);

        return ReviewMapper.toReviewDTO(saved, null);
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

        List<Review> reviews = reviewRepository.findAllByTripTripIdOrderByCreatedAtDesc(tripId);

        List<Long> ids = reviews.stream().map(Review::getReviewId).toList();

        List<Response> responses = responseRepository.findByReview_ReviewIdInAndDeletedFalse(ids);

        Map<Long, Response> responseIdMap = responses.stream().
                collect(Collectors.toMap(r-> r.getReview().getReviewId(),
                        response -> response));

        return reviews.stream()
                .map(review ->  ReviewMapper.toReviewDTO(review,
                        responseIdMap.get(review.getReviewId())))
                .collect(Collectors.toList());
    }

    @Nullable
    @Transactional(readOnly = true)
    public List<ReviewDTO> findByActivity(Long activityId) {

        List<Review> reviews = reviewRepository.findAllByActivityActivityIdOrderByCreatedAtDesc(activityId);

        List<Long> ids = reviews.stream().map(Review::getReviewId).toList();

        List<Response> responses = responseRepository.findByReview_ReviewIdInAndDeletedFalse(ids);

        Map<Long, Response> responseIdMap = responses.stream()
                .collect(Collectors.toMap(r-> r.getReview().getReviewId(),
                        response -> response));

        return reviews.stream()
                .map(review -> ReviewMapper.toReviewDTO(review,
                        responseIdMap.get(review.getReviewId())))
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
