package it.unical.ea_project.controller;

import it.unical.ea_project.domain.Activity;
import it.unical.ea_project.dto.ActivityDTO;
import it.unical.ea_project.dto.home.ActivityHomeDTO;
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


    //Recupera una singola attività completa.
    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(
            @PathVariable Long id
    ) {
        return activityService.getActivityDtoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

     //Recupera le attività più apprezzate.
    @GetMapping("/top")
    public ResponseEntity<List<ActivityDTO>> getTopActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        return ResponseEntity.ok(
                activityService.getTopActivityDtos(page, size)
        );
    }

    //Recupera le attività più apprezzate per la Home.
    @GetMapping("/top/cards")
    public ResponseEntity<List<ActivityHomeDTO>> getTopActivityCards(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size
    ) {
        return ResponseEntity.ok(
                activityService.getTopActivityHomeDtos(page, size)
        );
    }

    //Recupera le attività di una determinata categoria.
    @GetMapping("/category/{category}")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByCategory(
            @PathVariable Activity.Category category
    ) {
        return ResponseEntity.ok(
                activityService.getActivityDtosByCategory(category)
        );
    }
}