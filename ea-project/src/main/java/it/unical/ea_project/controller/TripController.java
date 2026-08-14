package it.unical.ea_project.controller;

import it.unical.ea_project.domain.Trip;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import it.unical.ea_project.service.TripService;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping("/published")
    public ResponseEntity<List<Trip>> getPublishedTrips() {
        return ResponseEntity.ok(tripService.getPublishedTrips());
    }

}
