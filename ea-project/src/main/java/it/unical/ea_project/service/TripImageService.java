package it.unical.ea_project.service;

import it.unical.ea_project.domain.TripImage;
import java.util.List;

public interface TripImageService {
    List<TripImage> getImagesByTripId(Long tripId);
    TripImage addImageToTrip(Long tripId, TripImage image);
    void deleteImage(Long imageId);
}