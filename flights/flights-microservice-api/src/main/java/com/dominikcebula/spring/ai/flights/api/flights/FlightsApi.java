package com.dominikcebula.spring.ai.flights.api.flights;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("/api/v1/flights")
public interface FlightsApi {

    @GetExchange
    List<Flight> getAllFlights(
            @RequestParam(required = false) String departure,
            @RequestParam(required = false) String arrival);

    @GetExchange("/{flightNumber}")
    Flight getFlightByNumber(@PathVariable String flightNumber);
}
