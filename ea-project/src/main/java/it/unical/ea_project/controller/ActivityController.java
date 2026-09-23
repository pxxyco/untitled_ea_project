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

    /**
     * Recupera una singola attività completa.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(
            @PathVariable Long id
    ) {
        return activityService.getActivityDtoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Recupera le attività più apprezzate.
     *
     * Restituisce il DTO completo.
     */
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

    /**
     * Ricerca attività per Home.
     *
     * Restituisce DTO leggeri.
     */
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

    /**
     * Suggerimenti per la barra di ricerca.
     */
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

    /**
     * Attività più apprezzate per la Home.
     *
     * Restituisce DTO leggeri.
     */
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

    /**
     * Attività complete appartenenti a una categoria.
     */
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