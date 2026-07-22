package it.unical.ea_project.controllers;

import it.unical.ea_project.services.StageService;
import it.unical.ea_project.services.TripService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/trips/{tripId}/stages")
public class StageController {

    private final StageService stageService;
    private final TripService tripService;

    public StageController(StageService stageService, TripService tripService) {
        this.stageService = stageService;
        this.tripService = tripService;
    }

}
