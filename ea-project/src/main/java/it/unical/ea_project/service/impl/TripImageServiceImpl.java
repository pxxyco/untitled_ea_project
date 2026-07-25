package it.unical.ea_project.service.impl;

import it.unical.ea_project.domain.Trip;
import it.unical.ea_project.domain.TripImage;
import it.unical.ea_project.repository.TripImageRepository;
import it.unical.ea_project.repository.TripRepository;
import it.unical.ea_project.service.TripImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TripImageServiceImpl implements TripImageService {

    private final TripImageRepository tripImageRepository;
    private final TripRepository tripRepository;

    @Override
    @Transactional(readOnly = true)
    public List<TripImage> getImagesByTripId(Long tripId) {

        Trip trip = tripRepository.findByTripIdAndDeletedAtIsNull(tripId)
                .orElseThrow(() -> new RuntimeException("Viaggio non trovato con ID: " + tripId));
        return tripImageRepository.findByTripOrderByOrderIndexAsc(trip);
    }

    @Override
    @Transactional
    public TripImage addImageToTrip(Long tripId, TripImage image) {
        Trip trip = tripRepository.findByTripIdAndDeletedAtIsNull(tripId)
                .orElseThrow(() -> new RuntimeException("Viaggio non trovato con ID: " + tripId));
        image.setTrip(trip);
        return tripImageRepository.save(image);
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        tripImageRepository.deleteById(imageId);
    }
}