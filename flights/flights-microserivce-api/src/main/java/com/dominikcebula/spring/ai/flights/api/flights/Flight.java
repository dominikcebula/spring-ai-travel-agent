package com.dominikcebula.spring.ai.flights.api.flights;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;

public record Flight(
        String flightNumber,
        String airlineCode,
        String airlineName,
        String departureAirportCode,
        String departureAirportName,
        String departureCity,
        String arrivalAirportCode,
        String arrivalAirportName,
        String arrivalCity,
        LocalTime departureTime,
        LocalTime arrivalTime,
        Duration flightDuration,
        String aircraftType,
        BigDecimal priceUsd,
        int availableSeats
) {
}
