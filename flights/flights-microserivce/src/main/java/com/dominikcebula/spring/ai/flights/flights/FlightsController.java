package com.dominikcebula.spring.ai.flights.flights;

import com.dominikcebula.spring.ai.flights.api.flights.Flight;
import com.dominikcebula.spring.ai.flights.api.flights.FlightsApi;
import com.dominikcebula.spring.ai.flights.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class FlightsController implements FlightsApi {

    private final FlightsService flightsService;

    public FlightsController(FlightsService flightsService) {
        this.flightsService = flightsService;
    }

    @Override
    public List<Flight> getAllFlights(String departure, String arrival) {
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

    @Override
    public Flight getFlightByNumber(String flightNumber) {
        return flightsService.getFlightByNumber(flightNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Flight not found: " + flightNumber));
    }
}
