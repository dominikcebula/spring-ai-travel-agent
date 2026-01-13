package com.dominikcebula.spring.ai.flights.flights;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping
    public List<Flight> getAllFlights(
            @RequestParam(required = false) String departure,
            @RequestParam(required = false) String arrival) {

        if (departure != null && arrival != null) {
            return flightService.getFlightsByRoute(departure, arrival);
        }
        if (departure != null) {
            return flightService.getFlightsByDepartureAirport(departure);
        }
        if (arrival != null) {
            return flightService.getFlightsByArrivalAirport(arrival);
        }

        return flightService.getAllFlights();
    }

    @GetMapping("/{flightNumber}")
    public ResponseEntity<Flight> getFlightByNumber(@PathVariable String flightNumber) {
        return flightService.getFlightByNumber(flightNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
