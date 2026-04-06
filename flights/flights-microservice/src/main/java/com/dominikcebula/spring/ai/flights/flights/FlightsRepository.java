package com.dominikcebula.spring.ai.flights.flights;

import com.dominikcebula.spring.ai.flights.api.flights.Flight;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public class FlightsRepository {

    private final List<Flight> flights = initializeFlights();

    public List<Flight> findAll() {
        return flights;
    }

    public Optional<Flight> findByFlightNumber(String flightNumber) {
        return flights.stream()
                .filter(flight -> flight.flightNumber().equals(flightNumber))
                .findFirst();
    }

    public List<Flight> findByDepartureAirport(String airportCode) {
        return flights.stream()
                .filter(flight -> flight.departureAirportCode().equals(airportCode))
                .toList();
    }

    public List<Flight> findByArrivalAirport(String airportCode) {
        return flights.stream()
                .filter(flight -> flight.arrivalAirportCode().equals(airportCode))
                .toList();
    }

    public List<Flight> findByRoute(String departureAirportCode, String arrivalAirportCode) {
        return flights.stream()
                .filter(flight -> flight.departureAirportCode().equals(departureAirportCode))
                .filter(flight -> flight.arrivalAirportCode().equals(arrivalAirportCode))
                .toList();
    }

    private List<Flight> initializeFlights() {
        return List.of(
                // US to Europe - Transatlantic routes
                createFlight("AA100", "AA", "American Airlines", "JFK", "John F. Kennedy International", "New York",
                        "LHR", "Heathrow", "London", LocalTime.of(19, 0), LocalTime.of(7, 15),
                        Duration.ofHours(7).plusMinutes(15), "Boeing 777-300ER", new BigDecimal("856.00"), 42),

                createFlight("UA900", "UA", "United Airlines", "EWR", "Newark Liberty International", "Newark",
                        "FRA", "Frankfurt Airport", "Frankfurt", LocalTime.of(18, 30), LocalTime.of(8, 20),
                        Duration.ofHours(8).plusMinutes(50), "Boeing 787-9", new BigDecimal("742.00"), 38),

                createFlight("DL40", "DL", "Delta Air Lines", "JFK", "John F. Kennedy International", "New York",
                        "AMS", "Amsterdam Schiphol", "Amsterdam", LocalTime.of(22, 0), LocalTime.of(11, 30),
                        Duration.ofHours(7).plusMinutes(30), "Airbus A330-900neo", new BigDecimal("698.00"), 55),

                createFlight("AA90", "AA", "American Airlines", "DFW", "Dallas/Fort Worth International", "Dallas",
                        "LHR", "Heathrow", "London", LocalTime.of(17, 45), LocalTime.of(8, 30),
                        Duration.ofHours(9).plusMinutes(45), "Boeing 787-9", new BigDecimal("923.00"), 29),

                createFlight("UA906", "UA", "United Airlines", "ORD", "O'Hare International", "Chicago",
                        "FRA", "Frankfurt Airport", "Frankfurt", LocalTime.of(17, 0), LocalTime.of(8, 10),
                        Duration.ofHours(9).plusMinutes(10), "Boeing 787-10", new BigDecimal("812.00"), 44),

                createFlight("DL200", "DL", "Delta Air Lines", "LAX", "Los Angeles International", "Los Angeles",
                        "AMS", "Amsterdam Schiphol", "Amsterdam", LocalTime.of(16, 30), LocalTime.of(12, 45),
                        Duration.ofHours(10).plusMinutes(15), "Airbus A350-900", new BigDecimal("945.00"), 37),

                createFlight("LO4", "LO", "LOT Polish Airlines", "JFK", "John F. Kennedy International", "New York",
                        "WAW", "Warsaw Chopin", "Warsaw", LocalTime.of(22, 30), LocalTime.of(13, 30),
                        Duration.ofHours(9), "Boeing 787-9", new BigDecimal("678.00"), 61),

                createFlight("LO2", "LO", "LOT Polish Airlines", "ORD", "O'Hare International", "Chicago",
                        "WAW", "Warsaw Chopin", "Warsaw", LocalTime.of(21, 45), LocalTime.of(14, 15),
                        Duration.ofHours(9).plusMinutes(30), "Boeing 787-8", new BigDecimal("652.00"), 48),

                createFlight("UA960", "UA", "United Airlines", "SFO", "San Francisco International", "San Francisco",
                        "FRA", "Frankfurt Airport", "Frankfurt", LocalTime.of(15, 20), LocalTime.of(11, 10),
                        Duration.ofHours(10).plusMinutes(50), "Boeing 787-9", new BigDecimal("978.00"), 33),

                createFlight("BA268", "BA", "British Airways", "SFO", "San Francisco International", "San Francisco",
                        "LHR", "Heathrow", "London", LocalTime.of(18, 45), LocalTime.of(12, 55),
                        Duration.ofHours(10).plusMinutes(10), "Boeing 777-300ER", new BigDecimal("1024.00"), 26),

                // Europe to US - Transatlantic routes
                createFlight("BA117", "BA", "British Airways", "LHR", "Heathrow", "London",
                        "JFK", "John F. Kennedy International", "New York", LocalTime.of(9, 30), LocalTime.of(12, 45),
                        Duration.ofHours(8).plusMinutes(15), "Airbus A380-800", new BigDecimal("876.00"), 52),

                createFlight("LH400", "LH", "Lufthansa", "FRA", "Frankfurt Airport", "Frankfurt",
                        "JFK", "John F. Kennedy International", "New York", LocalTime.of(10, 15), LocalTime.of(13, 5),
                        Duration.ofHours(8).plusMinutes(50), "Airbus A340-600", new BigDecimal("798.00"), 41),

                createFlight("KL641", "KL", "KLM Royal Dutch Airlines", "AMS", "Amsterdam Schiphol", "Amsterdam",
                        "LAX", "Los Angeles International", "Los Angeles", LocalTime.of(11, 0), LocalTime.of(13, 30),
                        Duration.ofHours(11).plusMinutes(30), "Boeing 787-10", new BigDecimal("892.00"), 35),

                createFlight("LO3", "LO", "LOT Polish Airlines", "WAW", "Warsaw Chopin", "Warsaw",
                        "JFK", "John F. Kennedy International", "New York", LocalTime.of(11, 30), LocalTime.of(15, 0),
                        Duration.ofHours(9).plusMinutes(30), "Boeing 787-9", new BigDecimal("658.00"), 57),

                createFlight("LH430", "LH", "Lufthansa", "FRA", "Frankfurt Airport", "Frankfurt",
                        "ORD", "O'Hare International", "Chicago", LocalTime.of(10, 0), LocalTime.of(12, 45),
                        Duration.ofHours(9).plusMinutes(45), "Boeing 747-8", new BigDecimal("834.00"), 39),

                createFlight("BA287", "BA", "British Airways", "LHR", "Heathrow", "London",
                        "SFO", "San Francisco International", "San Francisco", LocalTime.of(11, 15), LocalTime.of(14, 30),
                        Duration.ofHours(11).plusMinutes(15), "Boeing 777-300ER", new BigDecimal("998.00"), 28),

                createFlight("KL621", "KL", "KLM Royal Dutch Airlines", "AMS", "Amsterdam Schiphol", "Amsterdam",
                        "JFK", "John F. Kennedy International", "New York", LocalTime.of(10, 45), LocalTime.of(13, 15),
                        Duration.ofHours(8).plusMinutes(30), "Boeing 787-9", new BigDecimal("756.00"), 46),

                createFlight("LH438", "LH", "Lufthansa", "FRA", "Frankfurt Airport", "Frankfurt",
                        "DFW", "Dallas/Fort Worth International", "Dallas", LocalTime.of(9, 30), LocalTime.of(13, 45),
                        Duration.ofHours(10).plusMinutes(15), "Airbus A340-300", new BigDecimal("867.00"), 32),

                // Intra-Europe routes
                createFlight("LO281", "LO", "LOT Polish Airlines", "WAW", "Warsaw Chopin", "Warsaw",
                        "LHR", "Heathrow", "London", LocalTime.of(7, 15), LocalTime.of(9, 10),
                        Duration.ofHours(2).plusMinutes(55), "Embraer E195", new BigDecimal("189.00"), 72),

                createFlight("LO335", "LO", "LOT Polish Airlines", "WAW", "Warsaw Chopin", "Warsaw",
                        "FRA", "Frankfurt Airport", "Frankfurt", LocalTime.of(6, 45), LocalTime.of(8, 35),
                        Duration.ofHours(1).plusMinutes(50), "Embraer E190", new BigDecimal("156.00"), 68),

                createFlight("LO267", "LO", "LOT Polish Airlines", "WAW", "Warsaw Chopin", "Warsaw",
                        "AMS", "Amsterdam Schiphol", "Amsterdam", LocalTime.of(8, 30), LocalTime.of(10, 40),
                        Duration.ofHours(2).plusMinutes(10), "Boeing 737-800", new BigDecimal("178.00"), 54),

                createFlight("LO357", "LO", "LOT Polish Airlines", "KRK", "Krakow John Paul II", "Krakow",
                        "FRA", "Frankfurt Airport", "Frankfurt", LocalTime.of(6, 20), LocalTime.of(8, 15),
                        Duration.ofHours(1).plusMinutes(55), "Embraer E175", new BigDecimal("142.00"), 63),

                createFlight("LH1371", "LH", "Lufthansa", "FRA", "Frankfurt Airport", "Frankfurt",
                        "WAW", "Warsaw Chopin", "Warsaw", LocalTime.of(9, 0), LocalTime.of(10, 50),
                        Duration.ofHours(1).plusMinutes(50), "Airbus A320neo", new BigDecimal("168.00"), 58),

                createFlight("BA856", "BA", "British Airways", "LHR", "Heathrow", "London",
                        "WAW", "Warsaw Chopin", "Warsaw", LocalTime.of(14, 30), LocalTime.of(18, 25),
                        Duration.ofHours(2).plusMinutes(55), "Airbus A320", new BigDecimal("198.00"), 49),

                createFlight("KL1355", "KL", "KLM Royal Dutch Airlines", "AMS", "Amsterdam Schiphol", "Amsterdam",
                        "KRK", "Krakow John Paul II", "Krakow", LocalTime.of(12, 15), LocalTime.of(14, 30),
                        Duration.ofHours(2).plusMinutes(15), "Embraer E190", new BigDecimal("165.00"), 71),

                // US Domestic connections
                createFlight("AA1234", "AA", "American Airlines", "JFK", "John F. Kennedy International", "New York",
                        "LAX", "Los Angeles International", "Los Angeles", LocalTime.of(8, 0), LocalTime.of(11, 30),
                        Duration.ofHours(5).plusMinutes(30), "Boeing 777-200", new BigDecimal("342.00"), 67),

                createFlight("UA1547", "UA", "United Airlines", "SFO", "San Francisco International", "San Francisco",
                        "ORD", "O'Hare International", "Chicago", LocalTime.of(7, 45), LocalTime.of(13, 50),
                        Duration.ofHours(4).plusMinutes(5), "Boeing 737 MAX 9", new BigDecimal("278.00"), 82),

                createFlight("DL1892", "DL", "Delta Air Lines", "LAX", "Los Angeles International", "Los Angeles",
                        "JFK", "John F. Kennedy International", "New York", LocalTime.of(9, 15), LocalTime.of(17, 30),
                        Duration.ofHours(5).plusMinutes(15), "Airbus A321neo", new BigDecimal("356.00"), 59),

                createFlight("AA2156", "AA", "American Airlines", "DFW", "Dallas/Fort Worth International", "Dallas",
                        "SFO", "San Francisco International", "San Francisco", LocalTime.of(10, 30), LocalTime.of(12, 45),
                        Duration.ofHours(3).plusMinutes(15), "Boeing 737-800", new BigDecimal("246.00"), 74),

                createFlight("UA789", "UA", "United Airlines", "ORD", "O'Hare International", "Chicago",
                        "LAX", "Los Angeles International", "Los Angeles", LocalTime.of(14, 0), LocalTime.of(16, 20),
                        Duration.ofHours(4).plusMinutes(20), "Boeing 787-8", new BigDecimal("298.00"), 51)
        );
    }

    private Flight createFlight(String flightNumber, String airlineCode, String airlineName,
                                String departureAirportCode, String departureAirportName, String departureCity,
                                String arrivalAirportCode, String arrivalAirportName, String arrivalCity,
                                LocalTime departureTime, LocalTime arrivalTime, Duration flightDuration,
                                String aircraftType, BigDecimal priceUsd, int availableSeats) {
        return new Flight(
                flightNumber, airlineCode, airlineName,
                departureAirportCode, departureAirportName, departureCity,
                arrivalAirportCode, arrivalAirportName, arrivalCity,
                departureTime, arrivalTime, flightDuration,
                aircraftType, priceUsd, availableSeats
        );
    }
}
