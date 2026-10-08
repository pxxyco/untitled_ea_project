package it.unical.ea_project.controller;

import it.unical.ea_project.service.TripService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trips")
public class CalendarController {

    private final TripService tripService;

    public CalendarController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping("/{tripId}/export/ics")
    public ResponseEntity<byte[]> exportTripToIcs(@PathVariable Long tripId) {
        String icsContent = tripService.exportTripToIcs(tripId);
        byte[] bytes = icsContent.getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/calendar"));
        headers.setContentDispositionFormData("attachment", "viaggio_" + tripId + ".ics");

        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
}