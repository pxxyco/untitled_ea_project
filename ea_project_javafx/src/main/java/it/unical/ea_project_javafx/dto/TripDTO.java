package it.unical.ea_project_javafx.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripDTO {

    private Long tripId;
    private String title;
    private String description;
    private String destinationCountry;
    private String destinationCity;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalPrice;
    private Integer maxSeats;
    private Integer availableSeats;
    private String status;
    private BigDecimal averageRating;
    private String coverPhotoUrl;


    public TripDTO(Long tripId, String title, String description, String destinationCity,
                   BigDecimal totalPrice, Integer maxSeats, Integer availableSeats, String status) {
        this.tripId = tripId;
        this.title = title;
        this.description = description;
        this.destinationCity = destinationCity;
        this.totalPrice = totalPrice;
        this.maxSeats = maxSeats;
        this.availableSeats = availableSeats;
        this.status = status;
    }

    public int getSoldSeats() {
        if (maxSeats == null || availableSeats == null) {
            return 0;
        }
        return maxSeats - availableSeats;
    }

    public double getPriceAsDouble() {
        return totalPrice != null ? totalPrice.doubleValue() : 0.0;
    }
}