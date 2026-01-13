package com.dominikcebula.spring.ai.flights.flights;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlightService {

    private final FlightRepository flightRepository;

    public FlightService(FlightRepository flightRepository) {
        this.flightRepository = flightRepository;
    }

    public List<Flight> getAllFlights() {
        return flightRepository.findAll();
    }

    public Optional<Flight> getFlightByNumber(String flightNumber) {
        return flightRepository.findByFlightNumber(flightNumber);
    }

    public List<Flight> getFlightsByDepartureAirport(String airportCode) {
        return flightRepository.findByDepartureAirport(airportCode);
    }

    public List<Flight> getFlightsByArrivalAirport(String airportCode) {
        return flightRepository.findByArrivalAirport(airportCode);
    }

    public List<Flight> getFlightsByRoute(String departureAirportCode, String arrivalAirportCode) {
        return flightRepository.findByRoute(departureAirportCode, arrivalAirportCode);
    }
}
