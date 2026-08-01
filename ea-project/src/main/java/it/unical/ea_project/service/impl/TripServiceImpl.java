package it.unical.ea_project.service.impl;

import it.unical.ea_project.domain.Trip;
import it.unical.ea_project.domain.Trip.TripStatus;
import it.unical.ea_project.repository.TripRepository;
import it.unical.ea_project.service.TripService;
import it.unical.ea_project.domain.User;

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

    @Override
    public String exportTripToIcs(Long tripId) {
        Trip trip = tripRepository.findByTripIdAndDeletedAtIsNull(tripId)
                .orElseThrow(() -> new RuntimeException("Viaggio non trovato con ID: " + tripId));

        StringBuilder sb = new StringBuilder();

        sb.append("BEGIN:VCALENDAR\r\n");
        sb.append("VERSION:2.0\r\n");
        sb.append("PRODID:-//Unical Enterprise Applications//ea-project//IT\r\n");
        sb.append("CALSCALE:GREGORIAN\r\n");
        sb.append("BEGIN:VEVENT\r\n");

        String uid = trip.getIcsUid();
        if (uid == null || uid.isBlank()) {
            uid = "trip-" + trip.getTripId() + "-" + java.util.UUID.randomUUID() + "@ea-project.unical.it";
        }
        sb.append("UID:").append(uid).append("\r\n");

        if (trip.getTitle() != null) {
            sb.append("SUMMARY:").append(sanitizeIcsField(trip.getTitle())).append("\r\n");
        }
        if (trip.getDescription() != null) {
            sb.append("DESCRIPTION:").append(sanitizeIcsField(trip.getDescription())).append("\r\n");
        }

        String location = buildLocation(trip.getDestinationCity(), trip.getDestinationCountry());
        if (!location.isBlank()) {
            sb.append("LOCATION:").append(sanitizeIcsField(location)).append("\r\n");
        }

        java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd");

        if (trip.getStartDate() != null) {
            sb.append("DTSTART;VALUE=DATE:").append(trip.getStartDate().format(formatter)).append("\r\n");
        }
        if (trip.getEndDate() != null) {
            sb.append("DTEND;VALUE=DATE:").append(trip.getEndDate().plusDays(1).format(formatter)).append("\r\n");
        }

        sb.append("END:VEVENT\r\n");
        sb.append("END:VCALENDAR\r\n");

        return sb.toString();
    }

    private String buildLocation(String city, String country) {
        if (city != null && country != null) {
            return city + ", " + country;
        } else if (city != null) {
            return city;
        } else if (country != null) {
            return country;
        }
        return "";
    }

    private String sanitizeIcsField(String text) {
        if (text == null) return "";
        return text.replace("\n", "\\n").replace(",", "\\,").replace(";", "\\;");
    }
}