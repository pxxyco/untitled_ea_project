package it.unical.ea_project.service.impl;

import it.unical.ea_project.domain.Trip;
import it.unical.ea_project.domain.Trip.TripStatus;
import it.unical.ea_project.domain.User;
import it.unical.ea_project.dto.TripDTO;
import it.unical.ea_project.dto.home.TripHomeDTO;
import it.unical.ea_project.dto.home.TripSuggestionDTO;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Trip> getTripsByUser(User user) {
        return tripRepository.findByCreatedByAndDeletedAtIsNull(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDTO> getTripDtosByUser(User user) {
        return toDtoList(getTripsByUser(user));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trip> getPublishedTrips() {
        return tripRepository.findByStatusAndDeletedAtIsNull(TripStatus.PUBLISHED);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trip> searchByCountry(String country) {
        return tripRepository.findByDestinationCountryContainingIgnoreCaseAndDeletedAtIsNull(country);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trip> searchByCity(String city) {
        return tripRepository.findByDestinationCityContainingIgnoreCaseAndDeletedAtIsNull(city);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trip> getTripsInDateRange(LocalDate start, LocalDate end) {
        return tripRepository.findByStartDateGreaterThanEqualAndEndDateLessThanEqualAndDeletedAtIsNull(start, end);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trip> getAvailableTrips() {
        return tripRepository.findByStatusAndAvailableSeatsGreaterThanAndDeletedAtIsNull(TripStatus.PUBLISHED, 0);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Trip> getTripById(Long id) {
        return tripRepository.findByTripIdAndDeletedAtIsNull(id);
    }

    @Override
    @Transactional
    public Trip createTrip(Trip trip, Long creatorId) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato con ID: " + creatorId));
        trip.setCreatedBy(creator);
        if (trip.getStatus() == null) {
            trip.setStatus(TripStatus.PUBLISHED);
        }
        if (trip.getAvailableSeats() == null) {
            trip.setAvailableSeats(trip.getMaxSeats());
        }
        if (trip.getStages() != null) {
            trip.getStages().forEach(stage -> stage.setTrip(trip));
        }
        return tripRepository.save(trip);
    }

    @Override
    @Transactional
    public Trip updateTrip(Long id, Trip details) {
        Trip existing = getTripById(id)
                .orElseThrow(() -> new RuntimeException("Viaggio non trovato con ID: " + id));

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

        return tripRepository.save(existing);
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
    @Transactional
    public void deleteTrip(Long id) {
        getTripById(id).ifPresent(trip -> {
            trip.setDeletedAt(LocalDateTime.now());
            tripRepository.save(trip);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Trip> getTopTrips(int page, int size) {
        return tripRepository.findTopTrips(TripStatus.PUBLISHED, PageRequest.of(page, size));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TripDTO> getTripDtoById(Long id) {
        return getTripById(id).map(this::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDTO> getPublishedTripDtos() {
        return toDtoList(getPublishedTrips());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDTO> getAvailableTripDtos() {
        return toDtoList(getAvailableTrips());
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDTO> searchByCountryDtos(String country) {
        return toDtoList(searchByCountry(country));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDTO> searchByCityDtos(String city) {
        return toDtoList(searchByCity(city));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDTO> getTripsInDateRangeDtos(LocalDate start, LocalDate end) {
        return toDtoList(getTripsInDateRange(start, end));
    }

    @Override
    @Transactional
    public TripDTO createTripDto(Trip trip, Long creatorId) {
        return toDto(createTrip(trip, creatorId));
    }

    @Override
    @Transactional
    public TripDTO updateTripDto(Long id, Trip details) {
        return toDto(updateTrip(id, details));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripDTO> getTopTripDtos(int page, int size) {
        return toDtoList(getTopTrips(page, size));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripHomeDTO> searchTripHomeDtos(String query, LocalDate date, int page, int size) {
        String normalizedQuery = query == null ? "" : query.trim();
        Pageable pageable = PageRequest.of(page, size);

        List<Trip> trips = (date == null)
                ? tripRepository.searchPublished(normalizedQuery, TripStatus.PUBLISHED, pageable)
                : tripRepository.searchPublishedFromDate(normalizedQuery, TripStatus.PUBLISHED, date, pageable);

        return trips.stream().map(this::toHomeDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TripSuggestionDTO> getSuggestions(String query, int limit) {
        if (query == null || query.isBlank()) return List.of();
        return tripRepository.findSuggestions(query.trim(), TripStatus.PUBLISHED, PageRequest.of(0, limit));
    }

    private List<TripDTO> toDtoList(List<Trip> trips) {
        if (trips == null || trips.isEmpty()) return List.of();
        return trips.stream().map(this::toDto).toList();
    }

    private TripDTO toDto(Trip trip) {
        Long creatorId = trip.getCreatedBy() != null ? trip.getCreatedBy().getId() : null;

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

    private TripHomeDTO toHomeDto(Trip trip) {
        return new TripHomeDTO(
                trip.getTripId(),
                trip.getTitle(),
                buildLocation(trip.getDestinationCity(), trip.getDestinationCountry()),
                trip.getTotalPrice(),
                trip.getAverageRating(),
                trip.getCoverPhotoUrl(),
                trip.getStartDate(),
                trip.getEndDate()
        );
    }

    @Override
    public String exportTripToIcs(Long tripId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'exportTripToIcs'");
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