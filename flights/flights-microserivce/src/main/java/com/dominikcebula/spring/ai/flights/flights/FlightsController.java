package com.dominikcebula.spring.ai.flights.flights;

import com.dominikcebula.spring.ai.flights.api.flights.Flight;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/flights")
public class FlightsController {

    private final FlightsService flightsService;

    public FlightsController(FlightsService flightsService) {
        this.flightsService = flightsService;
    }

    @GetMapping
    public List<Flight> getAllFlights(
            @RequestParam(required = false) String departure,
            @RequestParam(required = false) String arrival) {

        if (departure != null && arrival != null) {
            return flightsService.getFlightsByRoute(departure, arrival);
        }
        if (departure != null) {
            return flightsService.getFlightsByDepartureAirport(departure);
        }
        if (arrival != null) {
            return flightsService.getFlightsByArrivalAirport(arrival);
        }

        return flightsService.getAllFlights();
    }

    @GetMapping("/{flightNumber}")
    public ResponseEntity<Flight> getFlightByNumber(@PathVariable String flightNumber) {
        return flightsService.getFlightByNumber(flightNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
