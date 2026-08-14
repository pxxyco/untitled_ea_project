package it.unical.ea_project.service;

import it.unical.ea_project.domain.Trip;
import it.unical.ea_project.domain.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TripService {

    List<Trip> getTripsByUser(User user);

    List<Trip> getPublishedTrips();

    List<Trip> searchByCountry(String country);

    List<Trip> searchByCity(String city);

    List<Trip> getTripsInDateRange(LocalDate start, LocalDate end);

    List<Trip> getAvailableTrips();

    Optional<Trip> getTripById(Long id);

    Trip createTrip(Trip trip, Long creatorId);

    Trip saveTrip(Trip trip);

    void deleteTripLogically(Trip trip);
}