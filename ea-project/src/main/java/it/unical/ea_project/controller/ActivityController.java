package it.unical.ea_project.controller;

import it.unical.ea_project.domain.Activity;
import it.unical.ea_project.dto.ActivityDTO;
import it.unical.ea_project.dto.home.ActivityHomeDTO;
import it.unical.ea_project.dto.home.ActivitySuggestionDTO;
import it.unical.ea_project.service.ActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(
            @PathVariable Long id
    ) {
        return activityService.getActivityDtoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/top")
    public ResponseEntity<List<ActivityDTO>> getTopActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        return ResponseEntity.ok(
                activityService.getTopActivityDtos(
                        page,
                        size
                )
        );
    }

    @GetMapping("/search")
    public ResponseEntity<List<ActivityHomeDTO>> searchActivities(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Activity.Category category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        return ResponseEntity.ok(
                activityService.searchActivityHomeDtos(
                        query,
                        category,
                        page,
                        size
                )
        );
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<ActivitySuggestionDTO>> suggestActivities(
            @RequestParam String query,
            @RequestParam(defaultValue = "6") int limit
    ) {
        return ResponseEntity.ok(
                activityService.getSuggestions(
                        query,
                        limit
                )
        );
    }

    @GetMapping("/top/cards")
    public ResponseEntity<List<ActivityHomeDTO>> getTopActivityCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        return ResponseEntity.ok(
                activityService.getTopActivityHomeDtos(
                        page,
                        size
                )
        );
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByCategory(
            @PathVariable Activity.Category category
    ) {
        return ResponseEntity.ok(
                activityService.getActivityDtosByCategory(
                        category
                )
        );
    }
}