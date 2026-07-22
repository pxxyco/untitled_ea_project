package it.unical.ea_project.service.impl;

import it.unical.ea_project.domain.Review;
import it.unical.ea_project.repository.ReviewRepository;
import it.unical.ea_project.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public Review crea(Review review) {
        return reviewRepository.save(review);
    }

    public Review findById(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review non trovata"));
    }

    public List<Review> findByUser(Long userId) {
        return reviewRepository.findAllByUserId(userId);
    }

    public List<Review> findByTrip(Long tripId) {
        return reviewRepository.findAllByTripTripId(tripId);
    }

    public List<Review> findByActivity(Long activityId) {
        return reviewRepository.findAllByActivityActivityId(activityId);
    }

    @Transactional
    public void elimina(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new RuntimeException("Review non trovata");
        }
        reviewRepository.softDeleteById(id);
    }
}
