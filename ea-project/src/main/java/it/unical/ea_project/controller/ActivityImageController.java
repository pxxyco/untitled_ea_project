package it.unical.ea_project.controller;

import it.unical.ea_project.domain.ActivityImage;
import it.unical.ea_project.service.ActivityImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity-images")
@RequiredArgsConstructor
public class ActivityImageController {

    private final ActivityImageService activityImageService;

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<List<ActivityImage>> getImagesByActivity(@PathVariable Long activityId) {
        return ResponseEntity.ok(activityImageService.getImagesByActivityId(activityId));
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