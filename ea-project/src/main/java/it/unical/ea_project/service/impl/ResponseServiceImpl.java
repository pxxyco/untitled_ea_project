package it.unical.ea_project.service.impl;


import it.unical.ea_project.domain.Response;
import it.unical.ea_project.domain.Review;
import it.unical.ea_project.domain.User;
import it.unical.ea_project.repositories.ReviewRepository;
import it.unical.ea_project.repositories.UserRepository;
import it.unical.ea_project.service.ResponseService;
import it.unical.ea_project.repositories.ResponseRepository;
//import it.unical.ea_project.repository.ReviewRepository;
//import it.unical.ea_project.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ResponseServiceImpl implements ResponseService {

    private final ResponseRepository responseRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;

    @Override
    @Transactional
    public Response createResponse(Long organizerId, Long reviewId, String comment) {
        User organizer = userRepository.findById(organizerId) //fixme
                .orElseThrow(() -> new IllegalArgumentException("Organizzatore non trovato con ID: " + organizerId));

        Review review = reviewRepository.findById(reviewId) //fixme
                .orElseThrow(() -> new IllegalArgumentException("Recensione non trovata con ID: " + reviewId));

        Response response = new Response();
        response.setOrganizer(organizer);
        response.setReview(review);
        response.setComment(comment);
        response.setCreatedAt(LocalDateTime.now());
        response.setDeleted(false);

        return responseRepository.save(response);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Response> getAllActiveResponses() {
        return responseRepository.findByDeletedFalse();
    }

    @Override
    @Transactional(readOnly = true)
    public Response getResponseById(Long id) {
        return responseRepository.findById(id)
                .filter(response -> !response.isDeleted())
                .orElseThrow(() -> new IllegalArgumentException("Risposta non trovata o eliminata con ID: " + id));
    }

    @Override
    @Transactional
    public void softDeleteResponse(Long id) {
        Response response = responseRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Risposta non trovata con ID: " + id));
        response.setDeleted(true);
        responseRepository.save(response);
    }
}