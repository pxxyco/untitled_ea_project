package it.unical.ea_project_javafx.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

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


    public TripDTO() {
    }

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



    public Long getTripId() {
        return tripId;
    }

    public void setTripId(Long tripId) {
        this.tripId = tripId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getMaxSeats() {
        return maxSeats;
    }

    public void setMaxSeats(Integer maxSeats) {
        this.maxSeats = maxSeats;
    }

    public Integer getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(Integer availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(BigDecimal averageRating) {
        this.averageRating = averageRating;
    }

    public String getCoverPhotoUrl() {
        return coverPhotoUrl;
    }

    public void setCoverPhotoUrl(String coverPhotoUrl) {
        this.coverPhotoUrl = coverPhotoUrl;
    }
}