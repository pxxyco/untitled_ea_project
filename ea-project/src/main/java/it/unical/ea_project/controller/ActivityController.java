package it.unical.ea_project.controller;

import it.unical.ea_project.domain.Activity;
import it.unical.ea_project.domain.ActivityImage;
import it.unical.ea_project.dto.ActivityResponseDto;
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
    private final ActivityImageService activityImageService;

    @GetMapping
    public ResponseEntity<List<ActivityResponseDto>> getAllActivities() {
        return ResponseEntity.ok(
                activityService.getAllActivities().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityResponseDto> getActivityById(@PathVariable Long id) {
        Activity activity = activityService.getActivityById(id);
        return ResponseEntity.ok(toDto(activity));
    }

    @PostMapping
    public ResponseEntity<ActivityResponseDto> createActivity(@RequestBody Activity activity, @RequestParam Long creatorId) {
        Activity saved = activityService.createActivity(activity, creatorId);
        return ResponseEntity.ok(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityResponseDto> updateActivity(@PathVariable Long id, @RequestBody Activity activity) {
        Activity updated = activityService.updateActivity(id, activity);
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    private ActivityResponseDto toDto(Activity activity) {
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
    }
}