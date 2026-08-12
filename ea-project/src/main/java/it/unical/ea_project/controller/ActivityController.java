package it.unical.ea_project.controller;

import it.unical.ea_project.domain.Activity;
import it.unical.ea_project.domain.ActivityImage;
import it.unical.ea_project.dto.ActivityResponseDto; // Crea questo DTO
import it.unical.ea_project.service.ActivityImageService;
import it.unical.ea_project.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final ActivityImageService activityImageService; // <-- Iniettiamo il servizio immagini

    @GetMapping
    public ResponseEntity<List<ActivityResponseDto>> getAllActivities() {
        List<ActivityResponseDto> dtos = activityService.getAllActivities().stream()
                .map(activity -> {
                    // Recupera la prima immagine dell'attività per mostrarla nella card
                    String imageUrl = activityImageService.getImagesByActivityId(activity.getActivityId()).stream()
                            .findFirst()
                            .map(ActivityImage::getImageUrl)
                            .orElse("");

                    return new ActivityResponseDto(
                            activity.getActivityId(),
                            activity.getTitle(),
                            activity.getDescription(),
                            activity.getCategory() != null ? activity.getCategory().name() : "OTHER",
                            activity.getCity(),
                            activity.getPrice(),
                            activity.getAverageRating(),
                            imageUrl
                    );
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponseDto> getActivityById(@PathVariable Long id) {
        Activity activity = activityService.getActivityById(id);

        String imageUrl = activityImageService.getImagesByActivityId(activity.getActivityId()).stream()
                .findFirst()
                .map(ActivityImage::getImageUrl)
                .orElse("");

        ActivityResponseDto dto = new ActivityResponseDto(
                activity.getActivityId(),
                activity.getTitle(),
                activity.getDescription(),
                activity.getCategory() != null ? activity.getCategory().name() : "OTHER",
                activity.getCity(),
                activity.getPrice(),
                activity.getAverageRating(),
                imageUrl
        );

        return ResponseEntity.ok(dto);
    }

    @PostMapping
    public ResponseEntity<Activity> createActivity(@RequestBody Activity activity, @RequestParam Long creatorId) {
        return ResponseEntity.ok(activityService.createActivity(activity, creatorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Activity> updateActivity(@PathVariable Long id, @RequestBody Activity activity) {
        return ResponseEntity.ok(activityService.updateActivity(id, activity));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }
}