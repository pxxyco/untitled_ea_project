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
        Activity activity = activityService.getActivityById(id);
        return ResponseEntity.ok(toDto(activity));
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

    private ActivityDTO toDto(Activity activity) {
        List<ActivityImageDTO> imageDtos = activityImageService.getImagesByActivityId(activity.getActivityId()).stream()
                .map(img -> ActivityImageDTO.builder()
                        .imageId(img.getImageId())
                        .activityId(activity.getActivityId())
                        .imageUrl(img.getImageUrl())
                        .orderIndex(img.getOrderIndex())
                        .build())
                .collect(Collectors.toList());

        Long creatorId = null;
        if (activity.getCreatedBy() != null) {
            creatorId = activity.getCreatedBy().getId();
        }

        return ActivityDTO.builder()
                .activityId(activity.getActivityId())
                .createdByUserId(creatorId)
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