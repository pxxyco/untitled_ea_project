package it.unical.ea_project.controller;

import it.unical.ea_project.domain.ActivityImage;
import it.unical.ea_project.dto.ActivityImageDTO;
import it.unical.ea_project.service.ActivityImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activity-images")
@RequiredArgsConstructor
public class ActivityImageController {

    private final ActivityImageService activityImageService;

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<List<ActivityImageDTO>> getImagesByActivity(@PathVariable Long activityId) {
        List<ActivityImageDTO> images = activityImageService.getImagesByActivityId(activityId).stream()
                .map(img -> ActivityImageDTO.builder()
                        .imageId(img.getImageId())
                        .imageUrl(img.getImageUrl())
                        .activityId(activityId)
                        .orderIndex(img.getOrderIndex()).build())
                .toList();
        return ResponseEntity.ok(images);
    }

    @PostMapping("/activity/{activityId}")
    public ResponseEntity<ActivityImage> addImage(@PathVariable Long activityId, @RequestBody ActivityImage image) {
        return ResponseEntity.ok(activityImageService.addImageToActivity(activityId, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        activityImageService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}