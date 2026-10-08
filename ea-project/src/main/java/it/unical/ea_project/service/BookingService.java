package it.unical.ea_project.service;


import it.unical.ea_project.domain.Booking;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public interface BookingService {

    Booking creaPrenotazioneTrip(
            Long userId,
            Long tripId,
            Integer seats,
            BigDecimal price
    );

    Booking creaPrenotazioneActivity(
            Long userId,
            Long activityId,
            Integer seats,
            BigDecimal price
    );

    void cancella(Long bookingId);

}
