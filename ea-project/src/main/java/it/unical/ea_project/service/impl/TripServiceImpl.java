package it.unical.ea_project.service.impl;

import it.unical.ea_project.domain.Trip;
import it.unical.ea_project.domain.Trip.TripStatus;
import it.unical.ea_project.domain.User;
import it.unical.ea_project.dto.home.TripHomeDTO;
import it.unical.ea_project.repository.TripRepository;
import it.unical.ea_project.repository.UserRepository;
import it.unical.ea_project.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;

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
    public Trip createTrip(Trip trip, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con ID: " + creatorId));
        trip.setCreatedBy(creator);
        return tripRepository.save(trip);
    }

    @Override
    public List<Trip> getTopTrips(int page, int size) {
        return tripRepository.findTopTrips(TripStatus.PUBLISHED, PageRequest.of(page, size));
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



    @Override
    public List<TripHomeDTO> searchTripHomeDtos(String query, LocalDate date, int page, int size) {
        String normalizedQuery = query == null ? "" : query.trim();
        Pageable pageable = PageRequest.of(page, size);

        List<Trip> trips = (date == null)
                ? tripRepository.searchPublished(normalizedQuery, TripStatus.PUBLISHED, pageable)
                : tripRepository.searchPublishedFromDate(normalizedQuery, TripStatus.PUBLISHED, date, pageable);

        return trips.stream().map(this::toHomeDto).toList();
    }

    private TripHomeDTO toHomeDto(Trip t) {
        return new TripHomeDTO(
                t.getTripId(),
                t.getTitle(),
                buildLocation(t.getDestinationCity(), t.getDestinationCountry()),
                t.getTotalPrice(),
                t.getAverageRating(),
                t.getCoverPhotoUrl(),
                t.getStartDate(),
                t.getEndDate()
        );
    }

    private String buildLocation(String city, String country) {
        boolean hasCity = city != null && !city.isBlank();
        boolean hasCountry = country != null && !country.isBlank();
        if (hasCity && hasCountry) return city + ", " + country;
        if (hasCity) return city;
        if (hasCountry) return country;
        return "";
    }
}

