package it.unical.ea_project.controller;

import it.unical.ea_project.domain.Trip;
import it.unical.ea_project.dto.TripResponseDto;
import it.unical.ea_project.security.ResourceNotFoundException;
import it.unical.ea_project.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @GetMapping("/published")
    public ResponseEntity<List<TripResponseDto>> getPublishedTrips() {
        return ResponseEntity.ok(toDtoList(tripService.getPublishedTrips()));
    }

    @GetMapping("/available")
    public ResponseEntity<List<TripResponseDto>> getAvailableTrips() {
        return ResponseEntity.ok(toDtoList(tripService.getAvailableTrips()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponseDto> getTripById(@PathVariable Long id) {
        Trip trip = tripService.getTripById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viaggio non trovato con ID: " + id));
        return ResponseEntity.ok(TripResponseDto.fromEntity(trip));
    }

    @GetMapping("/search/country")
    public ResponseEntity<List<TripResponseDto>> searchByCountry(@RequestParam String country) {
        return ResponseEntity.ok(toDtoList(tripService.searchByCountry(country)));
    }

    @GetMapping("/search/city")
    public ResponseEntity<List<TripResponseDto>> searchByCity(@RequestParam String city) {
        return ResponseEntity.ok(toDtoList(tripService.searchByCity(city)));
    }

    @GetMapping("/search/date-range")
    public ResponseEntity<List<TripResponseDto>> getTripsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(toDtoList(tripService.getTripsInDateRange(start, end)));
    }

    @PostMapping
    public ResponseEntity<TripResponseDto> createTrip(@RequestBody Trip trip, @RequestParam Long creatorId) {
        Trip saved = tripService.createTrip(trip, creatorId);
        return ResponseEntity.ok(TripResponseDto.fromEntity(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripResponseDto> updateTrip(@PathVariable Long id, @RequestBody Trip details) {
        Trip existing = tripService.getTripById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viaggio non trovato con ID: " + id));

        existing.setTitle(details.getTitle());
        existing.setDescription(details.getDescription());
        existing.setDestinationCountry(details.getDestinationCountry());
        existing.setDestinationCity(details.getDestinationCity());
        existing.setStartDate(details.getStartDate());
        existing.setEndDate(details.getEndDate());
        existing.setTotalPrice(details.getTotalPrice());
        existing.setMaxSeats(details.getMaxSeats());
        existing.setAvailableSeats(details.getAvailableSeats());
        existing.setStatus(details.getStatus());
        existing.setCoverPhotoUrl(details.getCoverPhotoUrl());

        Trip saved = tripService.saveTrip(existing);
        return ResponseEntity.ok(TripResponseDto.fromEntity(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        Trip existing = tripService.getTripById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Viaggio non trovato con ID: " + id));
        tripService.deleteTripLogically(existing);
        return ResponseEntity.noContent().build();
    }

    private List<TripResponseDto> toDtoList(List<Trip> trips) {
        return trips.stream().map(TripResponseDto::fromEntity).collect(Collectors.toList());
    }
}