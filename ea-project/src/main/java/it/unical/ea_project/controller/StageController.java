package it.unical.ea_project.controller;

import it.unical.ea_project.service.StageService;
import it.unical.ea_project.service.TripService;
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
