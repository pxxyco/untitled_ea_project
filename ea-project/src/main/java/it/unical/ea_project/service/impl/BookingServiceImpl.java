package it.unical.ea_project.service.impl;

import it.unical.ea_project.domain.Booking;
import it.unical.ea_project.repository.BookingRepository;
import it.unical.ea_project.repository.TripRepository;
import it.unical.ea_project.repository.UserRepository;
import it.unical.ea_project.service.BookingService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final TripRepository tripRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              UserRepository userRepository,
                              TripRepository tripRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.tripRepository = tripRepository;
    }

    @Transactional
    public Booking creaPrenotazioneTrip(Long userId, Long tripId, Integer seats, BigDecimal price) {
        Booking b = new Booking();
        b.setUser(userRepository.getReferenceById(userId));
        b.setTrip(tripRepository.getReferenceById(tripId));
        b.setSeats(seats);
        b.setBookingType(Booking.BookingType.TRIP);
        b.setBookingStatus(Booking.BookingStatus.PENDING);
        b.setPrice(price);
        return bookingRepository.save(b);
    }

    @Transactional
    public Booking creaPrenotazioneActivity(Long userId, Long activityId, Integer seats, BigDecimal price) {
        Booking b = new Booking();
        b.setUser(userRepository.getReferenceById(userId));
        b.setTrip(tripRepository.getReferenceById(activityId));
        b.setSeats(seats);
        b.setBookingType(Booking.BookingType.ACTIVITY);
        b.setBookingStatus(Booking.BookingStatus.PENDING);
        b.setPrice(price);
        return bookingRepository.save(b);
    }

    @Transactional
    public void cancella(Long bookingId) {
        Booking b = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Non trovato"));
        b.setBookingStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(b);
    }

}
