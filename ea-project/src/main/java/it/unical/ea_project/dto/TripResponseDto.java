package it.unical.ea_project.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TripResponseDto(
        Long tripId,
        String title,
        String description,
        String destinationCountry,
        String destinationCity,
        LocalDate startDate,
        LocalDate endDate,
        BigDecimal totalPrice,
        Integer availableSeats,
        String status,
        BigDecimal averageRating,
        String coverPhotoUrl
) {
    public static TripResponseDto fromEntity(it.unical.ea_project.domain.Trip trip) {
        return new TripResponseDto(
                trip.getTripId(),
                trip.getTitle(),
                trip.getDescription(),
                trip.getDestinationCountry(),
                trip.getDestinationCity(),
                trip.getStartDate(),
                trip.getEndDate(),
                trip.getTotalPrice(),
                trip.getAvailableSeats(),
                trip.getStatus() != null ? trip.getStatus().name() : null,
                trip.getAverageRating(),
                trip.getCoverPhotoUrl()
        );
    }
}