package it.unical.ea_project.repository;

import it.unical.ea_project.domain.Booking;
import it.unical.ea_project.domain.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.beans.Transient;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);

    List<Booking> findByBookingStatus(Booking.BookingStatus status);

    List<Booking> findByBookingTypeAndBookingStatus(Booking.BookingType type, Booking.BookingStatus status);

    // soft delete tramite campo deleted_at
    @Modifying
    @Transactional
    @Query("UPDATE Booking b SET b.deletedAt = CURRENT_TIMESTAMP WHERE b.bookingId = :id")
    void softDeleteById(Long id);

}
