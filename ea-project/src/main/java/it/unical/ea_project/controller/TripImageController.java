package it.unical.ea_project.controller;

import it.unical.ea_project.domain.TripImage;
import it.unical.ea_project.service.TripImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trip-images")
@RequiredArgsConstructor
public class TripImageController {

    private final TripImageService tripImageService;

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<TripImage>> getImagesByTrip(@PathVariable Long tripId) {
        return ResponseEntity.ok(tripImageService.getImagesByTripId(tripId));
    }

    @PostMapping("/trip/{tripId}")
    public ResponseEntity<TripImage> addImage(@PathVariable Long tripId, @RequestBody TripImage image) {
        return ResponseEntity.ok(tripImageService.addImageToTrip(tripId, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        tripImageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}