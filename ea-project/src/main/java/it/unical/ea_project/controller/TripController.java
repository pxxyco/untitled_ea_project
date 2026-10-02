package it.unical.ea_project.controller;

import it.unical.ea_project.domain.Trip;
import it.unical.ea_project.dto.TripDTO;
import it.unical.ea_project.dto.home.TripHomeDTO;
import it.unical.ea_project.dto.home.TripSuggestionDTO;
import it.unical.ea_project.security.AuthUser;
import it.unical.ea_project.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @GetMapping("/published")
    public ResponseEntity<List<TripDTO>> getPublishedTrips() {
        return ResponseEntity.ok(tripService.getPublishedTripDtos());
    }

    @GetMapping("/available")
    public ResponseEntity<List<TripDTO>> getAvailableTrips() {
        return ResponseEntity.ok(tripService.getAvailableTripDtos());
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<TripSuggestionDTO>> suggestTrips(@RequestParam String query, @RequestParam(defaultValue = "6") int limit) {
        return ResponseEntity.ok(tripService.getSuggestions(query, limit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripDTO> getTripById(@PathVariable Long id) {
        return tripService.getTripDtoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/country")
    public ResponseEntity<List<TripDTO>> searchByCountry(@RequestParam String country) {
        return ResponseEntity.ok(tripService.searchByCountryDtos(country));
    }

    @GetMapping("/search/city")
    public ResponseEntity<List<TripDTO>> searchByCity(@RequestParam String city) {
        return ResponseEntity.ok(tripService.searchByCityDtos(city));
    }

    @GetMapping("/search/date-range")
    public ResponseEntity<List<TripDTO>> getTripsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(tripService.getTripsInDateRangeDtos(start, end));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TripHomeDTO>> searchTrips(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size) {
        return ResponseEntity.ok(tripService.searchTripHomeDtos(query, date, page, size));
    }

    @GetMapping("/top")
    public ResponseEntity<List<TripDTO>> getTopTrips(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(tripService.getTopTripDtos(page, size));
    }

    @PostMapping
    public ResponseEntity<TripDTO> createTrip(@RequestBody Trip trip,
                                              @AuthenticationPrincipal AuthUser user) {
        trip.setTripId(null);
        return ResponseEntity.ok(tripService.createTripDto(trip, user.id()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripDTO> updateTrip(@PathVariable Long id,
                                              @RequestBody Trip details,
                                              @AuthenticationPrincipal AuthUser user) {
        var existing = tripService.getTripById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();
        if (!isOwner(existing.get(), user)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok(tripService.updateTripDto(id, details));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id,
                                           @AuthenticationPrincipal AuthUser user) {
        var existing = tripService.getTripById(id);
        if (existing.isEmpty()) return ResponseEntity.notFound().build();
        if (!isOwner(existing.get(), user)) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        tripService.deleteTrip(id);
        return ResponseEntity.noContent().build();
    }

    private boolean isOwner(Trip trip, AuthUser user) {
        return trip.getCreatedBy() != null && trip.getCreatedBy().getId().equals(user.id());
    }
}