package it.unical.ea_project.service.impl;

import it.unical.ea_project.domain.Review;
import it.unical.ea_project.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Transactional
    public Review crea(Review review) {
        return reviewRepository.save(review);
    }

    public Review trovaPerId(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review non trovata"));
    }

    public List<Review> trovaTutte() {
        return reviewRepository.findAll();
    }

    public List<Review> trovaPerUtente(Long userId) {
        return reviewRepository.findAllByUserId(userId);
    }

    public List<Review> trovaPerTrip(Long tripId) {
        return reviewRepository.findAllByTripTripId(tripId);
    }

    public List<Review> trovaPerActivity(Long activityId) {
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
