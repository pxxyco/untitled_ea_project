package it.unical.ea_project.repository;

import it.unical.ea_project.domain.Trip;
import it.unical.ea_project.domain.Trip.TripStatus;
import it.unical.ea_project.domain.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // Trova tutti i viaggi creati da un determinato utente che non sono stati eliminati logicamente
    List<Trip> findByCreatedByAndDeletedAtIsNull(User createdBy);

    // Trova i viaggi per stato (es. tutti quelli PUBLISHED)
    List<Trip> findByStatusAndDeletedAtIsNull(TripStatus status);

    // Cerca viaggi per destinazione (paese o città) escludendo quelli eliminati
    List<Trip> findByDestinationCountryContainingIgnoreCaseAndDeletedAtIsNull(String country);
    List<Trip> findByDestinationCityContainingIgnoreCaseAndDeletedAtIsNull(String city);

    // Trova i viaggi disponibili in un determinato intervallo di date
    List<Trip> findByStartDateGreaterThanEqualAndEndDateLessThanEqualAndDeletedAtIsNull(LocalDate startDate, LocalDate endDate);

    // Trova i viaggi che hanno ancora posti disponibili e sono pubblicati
    List<Trip> findByStatusAndAvailableSeatsGreaterThanAndDeletedAtIsNull(TripStatus status, Integer minSeats);

    // Serve per recuperare un viaggio specifico solo se attivo (usiamo questo metodo quando usiamo il soft delete "deletedAt")
    @Query("SELECT t FROM Trip t LEFT JOIN FETCH t.stages WHERE t.tripId = :tripId AND t.deletedAt IS NULL")
    Optional<Trip> findByTripIdAndDeletedAtIsNull(Long tripId);

    Boolean existsByTripIdAndDeletedAtIsNull(Long tripId);


    List<Trip> findByStatusAndDeletedAtIsNullOrderByAverageRatingDesc(TripStatus tripStatus, Pageable pageable);
}
