package it.unical.ea_project.controller;

import it.unical.ea_project.domain.Activity;
import it.unical.ea_project.dto.ActivityDTO;
import it.unical.ea_project.dto.ActivityImageDTO;
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
    public ResponseEntity<List<ActivityDTO>> getAllActivities() {
        return ResponseEntity.ok(
                activityService.getAllActivities().stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(@PathVariable Long id) {
        return activityService.getActivityById(id)
                .map(activity -> ResponseEntity.ok(toDto(activity)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ActivityDTO> createActivity(@RequestBody Activity activity, @RequestParam Long creatorId) {
        Activity saved = activityService.createActivity(activity, creatorId);
        return ResponseEntity.ok(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityDTO> updateActivity(@PathVariable Long id, @RequestBody Activity activity) {
        Activity updated = activityService.updateActivity(id, activity);
        return ResponseEntity.ok(toDto(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long id) {
        activityService.deleteActivity(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top")
    public ResponseEntity<List<ActivityDTO>> getTopActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {

        return ResponseEntity.ok(
                activityService.getTopActivities(page, size).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByCategory(@PathVariable Activity.Category category) {
        return ResponseEntity.ok(
                activityService.getActivitiesByCategory(category).stream()
                        .map(this::toDto)
                        .collect(Collectors.toList())
        );
    }

    private ActivityDTO toDto(Activity activity) {
        List<ActivityImageDTO> imageDtos = activityImageService.getImagesByActivityId(activity.getActivityId()).stream()
                .map(img -> ActivityImageDTO.builder()
                        .imageId(img.getImageId())
                        .activityId(activity.getActivityId())
                        .imageUrl(img.getImageUrl())
                        .orderIndex(img.getOrderIndex())
                        .build())
                .collect(Collectors.toList());

        return ActivityDTO.builder()
                .activityId(activity.getActivityId())
                .createdByUserId(activity.getCreatedBy() != null ? activity.getCreatedBy().getId() : null)
                .title(activity.getTitle())
                .description(activity.getDescription())
                .category(activity.getCategory() != null ? activity.getCategory().name() : "OTHER")
                .latitude(activity.getLatitude())
                .longitude(activity.getLongitude())
                .placeName(activity.getPlaceName())
                .city(activity.getCity())
                .startDate(activity.getStartDate())
                .endDate(activity.getEndDate())
                .durationMinutes(activity.getDurationMinutes())
                .price(activity.getPrice())
                .maxSeats(activity.getMaxSeats())
                .availableSeats(activity.getAvailableSeats())
                .status(activity.getStatus() != null ? activity.getStatus().name() : null)
                .averageRating(activity.getAverageRating())
                .notes(activity.getNotes())
                .createdAt(activity.getCreatedAt())
                .updatedAt(activity.getUpdatedAt())
                .deletedAt(activity.getDeletedAt())
                .images(imageDtos)
                .build();
    }
}