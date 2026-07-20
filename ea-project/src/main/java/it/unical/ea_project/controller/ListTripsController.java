package it.unical.ea_project.controller;

import it.unical.ea_project.domain.ListTrips;
import it.unical.ea_project.service.ListTripsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/list-trips")
@RequiredArgsConstructor
public class ListTripsController {

    private final ListTripsService listTripsService;

    public record AddTripRequest(Long listId, Long tripId) {}

    @PostMapping
    public ResponseEntity<ListTrips> addTripToList(@RequestBody AddTripRequest request) {
        ListTrips addedTrip = listTripsService.addTripToList(request.listId(), request.tripId());
        return new ResponseEntity<>(addedTrip, HttpStatus.CREATED);
    }

    @GetMapping("/list/{listId}")
    public ResponseEntity<List<ListTrips>> getTripsByList(@PathVariable Long listId) {
        return ResponseEntity.ok(listTripsService.getTripsByListId(listId));
    }

    @DeleteMapping("/list/{listId}/trip/{tripId}")
    public ResponseEntity<Void> removeTripFromList(@PathVariable Long listId, @PathVariable Long tripId) {
        listTripsService.removeTripFromList(listId, tripId);
        return ResponseEntity.noContent().build();
    }
}