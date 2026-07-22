package it.unical.ea_project.controller;

import org.springframework.web.bind.annotation.*;

import it.unical.ea_project.service.TripService;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

}
