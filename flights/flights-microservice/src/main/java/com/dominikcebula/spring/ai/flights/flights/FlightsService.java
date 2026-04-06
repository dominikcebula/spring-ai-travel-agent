package com.dominikcebula.spring.ai.flights.flights;

import com.dominikcebula.spring.ai.flights.api.flights.Flight;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FlightsService {

    private final FlightsRepository flightsRepository;

    public FlightsService(FlightsRepository flightsRepository) {
        this.flightsRepository = flightsRepository;
    }

    public List<Flight> getAllFlights() {
        return flightsRepository.findAll();
    }

    public Optional<Flight> getFlightByNumber(String flightNumber) {
        return flightsRepository.findByFlightNumber(flightNumber);
    }

    public List<Flight> getFlightsByDepartureAirport(String airportCode) {
        return flightsRepository.findByDepartureAirport(airportCode);
    }

    public List<Flight> getFlightsByArrivalAirport(String airportCode) {
        return flightsRepository.findByArrivalAirport(airportCode);
    }

    public List<Flight> getFlightsByRoute(String departureAirportCode, String arrivalAirportCode) {
        return flightsRepository.findByRoute(departureAirportCode, arrivalAirportCode);
    }
}
