package it.unical.ea_project.services;

import it.unical.ea_project.domain.Trip;
import it.unical.ea_project.domain.Trip.TripStatus;
import it.unical.ea_project.domain.User;
import it.unical.ea_project.repositories.TripRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TripService {

    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    public List<Trip> getTripsByUser(User user) {
        return tripRepository.findByCreatedByAndDeletedAtIsNull(user);
    }

    public List<Trip> getPublishedTrips() {
        return tripRepository.findByStatusAndDeletedAtIsNull(TripStatus.PUBLISHED);
    }

    public List<Trip> searchByCountry(String country) {
        return tripRepository.findByDestinationCountryContainingIgnoreCaseAndDeletedAtIsNull(country);
    }

    public List<Trip> searchByCity(String city) {
        return tripRepository.findByDestinationCityContainingIgnoreCaseAndDeletedAtIsNull(city);
    }

    public List<Trip> getTripsInDateRange(LocalDate start, LocalDate end) {
        return tripRepository.findByStartDateGreaterThanEqualAndEndDateLessThanEqualAndDeletedAtIsNull(start, end);
    }

    public List<Trip> getAvailableTrips() {
        return tripRepository.findByStatusAndAvailableSeatsGreaterThanAndDeletedAtIsNull(TripStatus.PUBLISHED, 0);
    }

    public Optional<Trip> getTripById(Long id) {
        return tripRepository.findByTripIdAndDeletedAtIsNull(id);
    }

    @Transactional
    public Trip saveTrip(Trip trip) {
        return tripRepository.save(trip);
    }

    @Transactional
    public void deleteTripLogically(Trip trip) {
        trip.setDeletedAt(java.time.LocalDateTime.now());
        tripRepository.save(trip);
    }
}
