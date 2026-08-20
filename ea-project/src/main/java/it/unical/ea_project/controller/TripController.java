package it.unical.ea_project.controller;

import it.unical.ea_project.domain.Trip;
import it.unical.ea_project.dto.TripDTO;
import it.unical.ea_project.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
public class TripController {

    private final TripService tripService;

    @GetMapping("/published")
    public ResponseEntity<List<TripDTO>> getPublishedTrips() {
        return ResponseEntity.ok(toDtoList(tripService.getPublishedTrips()));
    }

    @GetMapping("/available")
    public ResponseEntity<List<TripDTO>> getAvailableTrips() {
        return ResponseEntity.ok(toDtoList(tripService.getAvailableTrips()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripDTO> getTripById(@PathVariable Long id) {
        return tripService.getTripById(id)
                .map(trip -> ResponseEntity.ok(toDto(trip)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/country")
    public ResponseEntity<List<TripDTO>> searchByCountry(@RequestParam String country) {
        return ResponseEntity.ok(toDtoList(tripService.searchByCountry(country)));
    }

    @GetMapping("/search/city")
    public ResponseEntity<List<TripDTO>> searchByCity(@RequestParam String city) {
        return ResponseEntity.ok(toDtoList(tripService.searchByCity(city)));
    }

    @GetMapping("/search/date-range")
    public ResponseEntity<List<TripDTO>> getTripsInDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(toDtoList(tripService.getTripsInDateRange(start, end)));
    }

    @PostMapping
    public ResponseEntity<TripDTO> createTrip(@RequestBody Trip trip, @RequestParam Long creatorId) {
        Trip saved = tripService.createTrip(trip, creatorId);
        return ResponseEntity.ok(toDto(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TripDTO> updateTrip(@PathVariable Long id, @RequestBody Trip details) {
        return tripService.getTripById(id)
                .map(existing -> {
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
                    return ResponseEntity.ok(toDto(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id) {
        return tripService.getTripById(id)
                .map(existing -> {
                    tripService.deleteTripLogically(existing);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private List<TripDTO> toDtoList(List<Trip> trips) {
        return trips.stream().map(this::toDto).collect(Collectors.toList());
    }

    private TripDTO toDto(Trip trip) {
        Long creatorId = null;
        if (trip.getCreatedBy() != null) {
            creatorId = trip.getCreatedBy().getId();
        }

        return TripDTO.builder()
                .tripId(trip.getTripId())
                .createdByUserId(creatorId)
                .title(trip.getTitle())
                .description(trip.getDescription())
                .destinationCountry(trip.getDestinationCountry())
                .destinationCity(trip.getDestinationCity())
                .startDate(trip.getStartDate())
                .endDate(trip.getEndDate())
                .totalPrice(trip.getTotalPrice())
                .maxSeats(trip.getMaxSeats())
                .availableSeats(trip.getAvailableSeats())
                .status(trip.getStatus() != null ? trip.getStatus().name() : null)
                .icsUid(trip.getIcsUid())
                .averageRating(trip.getAverageRating())
                .coverPhotoUrl(trip.getCoverPhotoUrl())
                .createdAt(trip.getCreatedAt())
                .updatedAt(trip.getUpdatedAt())
                .deletedAt(trip.getDeletedAt())
                .stages(Collections.emptyList())
                .build();
    }
}