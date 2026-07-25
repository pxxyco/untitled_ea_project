package it.unical.ea_project.service;

import it.unical.ea_project.domain.Review;

import java.util.List;

public interface ReviewService {

    Review crea(Review review);

    Review findById(Long id);

    List<Review> findByUser(Long userId);

    List<Review> findByActivity(Long activityId);

    List<Review> findByTrip(Long tripId);

    void elimina(Long id);

}
