package it.unical.ea_project.services.impl;

import it.unical.ea_project.domain.Trip;
import it.unical.ea_project.domain.Trip.TripStatus;
import it.unical.ea_project.domain.User;
import it.unical.ea_project.repositories.TripRepository;
import it.unical.ea_project.services.TripService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    public TripServiceImpl(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    @Override
    public List<Trip> getTripsByUser(User user) {
        return tripRepository.findByCreatedByAndDeletedAtIsNull(user);
    }

    @Override
    public List<Trip> getPublishedTrips() {
        return tripRepository.findByStatusAndDeletedAtIsNull(TripStatus.PUBLISHED);
    }

    @Override
    public List<Trip> searchByCountry(String country) {
        return tripRepository.findByDestinationCountryContainingIgnoreCaseAndDeletedAtIsNull(country);
    }

    @Override
    public List<Trip> searchByCity(String city) {
        return tripRepository.findByDestinationCityContainingIgnoreCaseAndDeletedAtIsNull(city);
    }

    @Override
    public List<Trip> getTripsInDateRange(LocalDate start, LocalDate end) {
        return tripRepository.findByStartDateGreaterThanEqualAndEndDateLessThanEqualAndDeletedAtIsNull(start, end);
    }

    @Override
    public List<Trip> getAvailableTrips() {
        return tripRepository.findByStatusAndAvailableSeatsGreaterThanAndDeletedAtIsNull(TripStatus.PUBLISHED, 0);
    }

    @Override
    public Optional<Trip> getTripById(Long id) {
        return tripRepository.findByTripIdAndDeletedAtIsNull(id);
    }

    @Override
    @Transactional
    public Trip saveTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    @Override
    @Transactional
    public void deleteTripLogically(Trip trip) {
        trip.setDeletedAt(LocalDateTime.now());
        tripRepository.save(trip);
    }
}