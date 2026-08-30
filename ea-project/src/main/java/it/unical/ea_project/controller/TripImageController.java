package it.unical.ea_project.controller;

import it.unical.ea_project.domain.TripImage;
import it.unical.ea_project.dto.TripImageDTO;
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
    public ResponseEntity<List<TripImageDTO>> getImagesByTrip(@PathVariable Long tripId) {
        List<TripImageDTO> images = tripImageService.getImagesByTripId(tripId).stream()
                .map(img -> TripImageDTO.builder()
                        .imageId(img.getImageId())
                        .imageUrl(img.getImageUrl())
                        .tripId(tripId)
                        .orderIndex(img.getOrderIndex()).build())
                .toList();
        return ResponseEntity.ok(images);
    }

    @PostMapping("/trip/{tripId}")
    public ResponseEntity<TripImageDTO> addImage(@PathVariable Long tripId, @RequestBody TripImage image) {
        TripImage saved = tripImageService.addImageToTrip(tripId, image);
        TripImageDTO dto = TripImageDTO.builder()
                .imageId(saved.getImageId())
                .imageUrl(saved.getImageUrl())
                .tripId(tripId)
                .orderIndex(saved.getOrderIndex()).build();
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        tripImageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}