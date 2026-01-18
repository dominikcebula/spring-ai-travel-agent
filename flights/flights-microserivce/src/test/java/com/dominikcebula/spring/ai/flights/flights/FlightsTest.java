package com.dominikcebula.spring.ai.flights.flights;

import com.dominikcebula.spring.ai.flights.api.flights.Flight;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class FlightsTest {

    private static final String FLIGHTS_URL = "/api/v1/flights";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldListAllFlights() {
        // given
        // flights are pre-loaded in FlightRepository

        // when
        ResponseEntity<List<Flight>> response = restTemplate.exchange(
                FLIGHTS_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(30);
    }

    @Test
    void shouldReturnFlightsWithAllRequiredIataStyleFields() {
        // given
        // flights are pre-loaded in FlightRepository

        // when
        ResponseEntity<List<Flight>> response = restTemplate.exchange(
                FLIGHTS_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).allSatisfy(flight -> {
            assertThat(flight.flightNumber()).isNotBlank();
            assertThat(flight.airlineCode()).hasSize(2);
            assertThat(flight.airlineName()).isNotBlank();
            assertThat(flight.departureAirportCode()).hasSize(3);
            assertThat(flight.departureAirportName()).isNotBlank();
            assertThat(flight.departureCity()).isNotBlank();
            assertThat(flight.arrivalAirportCode()).hasSize(3);
            assertThat(flight.arrivalAirportName()).isNotBlank();
            assertThat(flight.arrivalCity()).isNotBlank();
            assertThat(flight.departureTime()).isNotNull();
            assertThat(flight.arrivalTime()).isNotNull();
            assertThat(flight.flightDuration()).isNotNull();
            assertThat(flight.flightDuration().toMinutes()).isPositive();
            assertThat(flight.aircraftType()).isNotBlank();
            assertThat(flight.priceUsd()).isPositive();
            assertThat(flight.availableSeats()).isPositive();
        });
    }

    @Test
    void shouldRetrieveAmericanAirlinesFlightAA100WithAllDetails() {
        // given
        String flightNumber = "AA100";

        // when
        ResponseEntity<Flight> response = restTemplate.getForEntity(
                FLIGHTS_URL + "/" + flightNumber,
                Flight.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertFlight(response.getBody(),
                "AA100", "AA", "American Airlines",
                "JFK", "John F. Kennedy International", "New York",
                "LHR", "Heathrow", "London",
                LocalTime.of(19, 0), LocalTime.of(7, 15), Duration.ofHours(7).plusMinutes(15),
                "Boeing 777-300ER", new BigDecimal("856.00"), 42);
    }

    @Test
    void shouldRetrieveBritishAirwaysFlightBA117WithAllDetails() {
        // given
        String flightNumber = "BA117";

        // when
        ResponseEntity<Flight> response = restTemplate.getForEntity(
                FLIGHTS_URL + "/" + flightNumber,
                Flight.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertFlight(response.getBody(),
                "BA117", "BA", "British Airways",
                "LHR", "Heathrow", "London",
                "JFK", "John F. Kennedy International", "New York",
                LocalTime.of(9, 30), LocalTime.of(12, 45), Duration.ofHours(8).plusMinutes(15),
                "Airbus A380-800", new BigDecimal("876.00"), 52);
    }

    @Test
    void shouldRetrieveLotPolishAirlinesFlightLO4WithAllDetails() {
        // given
        String flightNumber = "LO4";

        // when
        ResponseEntity<Flight> response = restTemplate.getForEntity(
                FLIGHTS_URL + "/" + flightNumber,
                Flight.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertFlight(response.getBody(),
                "LO4", "LO", "LOT Polish Airlines",
                "JFK", "John F. Kennedy International", "New York",
                "WAW", "Warsaw Chopin", "Warsaw",
                LocalTime.of(22, 30), LocalTime.of(13, 30), Duration.ofHours(9),
                "Boeing 787-9", new BigDecimal("678.00"), 61);
    }

    @Test
    void shouldRetrieveIntraEuropeanFlightLO357WithAllDetails() {
        // given
        String flightNumber = "LO357";

        // when
        ResponseEntity<Flight> response = restTemplate.getForEntity(
                FLIGHTS_URL + "/" + flightNumber,
                Flight.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertFlight(response.getBody(),
                "LO357", "LO", "LOT Polish Airlines",
                "KRK", "Krakow John Paul II", "Krakow",
                "FRA", "Frankfurt Airport", "Frankfurt",
                LocalTime.of(6, 20), LocalTime.of(8, 15), Duration.ofHours(1).plusMinutes(55),
                "Embraer E175", new BigDecimal("142.00"), 63);
    }

    @Test
    void shouldRetrieveUsDomesticFlightAA1234WithAllDetails() {
        // given
        String flightNumber = "AA1234";

        // when
        ResponseEntity<Flight> response = restTemplate.getForEntity(
                FLIGHTS_URL + "/" + flightNumber,
                Flight.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertFlight(response.getBody(),
                "AA1234", "AA", "American Airlines",
                "JFK", "John F. Kennedy International", "New York",
                "LAX", "Los Angeles International", "Los Angeles",
                LocalTime.of(8, 0), LocalTime.of(11, 30), Duration.ofHours(5).plusMinutes(30),
                "Boeing 777-200", new BigDecimal("342.00"), 67);
    }

    @Test
    void shouldReturnNotFoundForNonExistentFlight() {
        // given
        String nonExistentFlightNumber = "ZZ9999";

        // when
        ResponseEntity<Flight> response = restTemplate.getForEntity(
                FLIGHTS_URL + "/" + nonExistentFlightNumber,
                Flight.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldFilterFlightsByDepartureAirportJFK() {
        // given
        String departureAirport = "JFK";

        // when
        ResponseEntity<List<Flight>> response = restTemplate.exchange(
                FLIGHTS_URL + "?departure=" + departureAirport,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).allSatisfy(flight -> {
            assertThat(flight.departureAirportCode()).isEqualTo("JFK");
            assertThat(flight.departureAirportName()).isEqualTo("John F. Kennedy International");
            assertThat(flight.departureCity()).isEqualTo("New York");
        });
    }

    @Test
    void shouldFilterFlightsByDepartureAirportWAW() {
        // given
        String departureAirport = "WAW";

        // when
        ResponseEntity<List<Flight>> response = restTemplate.exchange(
                FLIGHTS_URL + "?departure=" + departureAirport,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).allSatisfy(flight -> {
            assertThat(flight.departureAirportCode()).isEqualTo("WAW");
            assertThat(flight.departureAirportName()).isEqualTo("Warsaw Chopin");
            assertThat(flight.departureCity()).isEqualTo("Warsaw");
        });

        assertThat(response.getBody())
                .extracting(Flight::arrivalAirportCode)
                .containsAnyOf("LHR", "FRA", "AMS", "JFK");
    }

    @Test
    void shouldFilterFlightsByArrivalAirportWAW() {
        // given
        String arrivalAirport = "WAW";

        // when
        ResponseEntity<List<Flight>> response = restTemplate.exchange(
                FLIGHTS_URL + "?arrival=" + arrivalAirport,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).allSatisfy(flight -> {
            assertThat(flight.arrivalAirportCode()).isEqualTo("WAW");
            assertThat(flight.arrivalAirportName()).isEqualTo("Warsaw Chopin");
            assertThat(flight.arrivalCity()).isEqualTo("Warsaw");
        });

        assertThat(response.getBody())
                .anyMatch(flight -> flight.airlineCode().equals("LO") &&
                        flight.airlineName().equals("LOT Polish Airlines"));
    }

    @Test
    void shouldFilterFlightsByRouteJFKtoLHR() {
        // given
        String departureAirport = "JFK";
        String arrivalAirport = "LHR";

        // when
        ResponseEntity<List<Flight>> response = restTemplate.exchange(
                FLIGHTS_URL + "?departure=" + departureAirport + "&arrival=" + arrivalAirport,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isNotEmpty();
        assertThat(response.getBody()).allSatisfy(flight -> {
            assertThat(flight.departureAirportCode()).isEqualTo("JFK");
            assertThat(flight.departureAirportName()).isEqualTo("John F. Kennedy International");
            assertThat(flight.departureCity()).isEqualTo("New York");
            assertThat(flight.arrivalAirportCode()).isEqualTo("LHR");
            assertThat(flight.arrivalAirportName()).isEqualTo("Heathrow");
            assertThat(flight.arrivalCity()).isEqualTo("London");
        });
    }

    @Test
    void shouldReturnEmptyListForNonExistentRoute() {
        // given
        String departureAirport = "KRK";
        String arrivalAirport = "LAX";

        // when
        ResponseEntity<List<Flight>> response = restTemplate.exchange(
                FLIGHTS_URL + "?departure=" + departureAirport + "&arrival=" + arrivalAirport,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void shouldReturnEmptyListForNonExistentDepartureAirport() {
        // given
        String nonExistentAirport = "XXX";

        // when
        ResponseEntity<List<Flight>> response = restTemplate.exchange(
                FLIGHTS_URL + "?departure=" + nonExistentAirport,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void shouldContainTransatlanticRoutesInBothDirections() {
        // given
        // flights are pre-loaded in FlightRepository

        // when
        ResponseEntity<List<Flight>> response = restTemplate.exchange(
                FLIGHTS_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        List<String> usAirports = List.of("JFK", "LAX", "SFO", "ORD", "DFW");
        List<String> euAirports = List.of("LHR", "FRA", "AMS", "WAW", "KRK");

        List<Flight> usToEuFlights = response.getBody().stream()
                .filter(f -> usAirports.contains(f.departureAirportCode()) &&
                        euAirports.contains(f.arrivalAirportCode()))
                .toList();

        List<Flight> euToUsFlights = response.getBody().stream()
                .filter(f -> euAirports.contains(f.departureAirportCode()) &&
                        usAirports.contains(f.arrivalAirportCode()))
                .toList();

        assertThat(usToEuFlights).isNotEmpty();
        assertThat(euToUsFlights).isNotEmpty();

        assertThat(usToEuFlights).allSatisfy(flight -> {
            assertThat(flight.flightDuration().toHours()).isGreaterThanOrEqualTo(7);
            assertThat(flight.priceUsd()).isGreaterThan(new BigDecimal("500.00"));
        });

        assertThat(euToUsFlights).allSatisfy(flight -> {
            assertThat(flight.flightDuration().toHours()).isGreaterThanOrEqualTo(8);
            assertThat(flight.priceUsd()).isGreaterThan(new BigDecimal("600.00"));
        });
    }

    @Test
    void shouldContainFlightsFromAllExpectedAirlines() {
        // given
        // flights are pre-loaded in FlightRepository

        // when
        ResponseEntity<List<Flight>> response = restTemplate.exchange(
                FLIGHTS_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody())
                .extracting(Flight::airlineCode)
                .contains("AA", "UA", "DL", "BA", "LH", "KL", "LO");

        assertThat(response.getBody())
                .extracting(Flight::airlineName)
                .contains(
                        "American Airlines",
                        "United Airlines",
                        "Delta Air Lines",
                        "British Airways",
                        "Lufthansa",
                        "KLM Royal Dutch Airlines",
                        "LOT Polish Airlines"
                );
    }

    @Test
    void shouldContainIntraEuropeanShortHaulFlights() {
        // given
        List<String> euAirports = List.of("LHR", "FRA", "AMS", "WAW", "KRK");

        // when
        ResponseEntity<List<Flight>> response = restTemplate.exchange(
                FLIGHTS_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        List<Flight> intraEuFlights = response.getBody().stream()
                .filter(f -> euAirports.contains(f.departureAirportCode()) &&
                        euAirports.contains(f.arrivalAirportCode()))
                .toList();

        assertThat(intraEuFlights).isNotEmpty();
        assertThat(intraEuFlights).allSatisfy(flight -> {
            assertThat(flight.flightDuration().toHours()).isLessThanOrEqualTo(3);
            assertThat(flight.priceUsd()).isLessThan(new BigDecimal("300.00"));
        });
    }

    @Test
    void shouldContainUsDomesticFlights() {
        // given
        List<String> usAirports = List.of("JFK", "LAX", "SFO", "ORD", "DFW");

        // when
        ResponseEntity<List<Flight>> response = restTemplate.exchange(
                FLIGHTS_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        List<Flight> domesticFlights = response.getBody().stream()
                .filter(f -> usAirports.contains(f.departureAirportCode()) &&
                        usAirports.contains(f.arrivalAirportCode()))
                .toList();

        assertThat(domesticFlights).isNotEmpty();
        assertThat(domesticFlights).allSatisfy(flight -> {
            assertThat(flight.flightDuration().toHours()).isLessThanOrEqualTo(6);
            assertThat(flight.priceUsd()).isLessThan(new BigDecimal("500.00"));
        });
    }

    @Test
    void shouldHaveRealisticAircraftTypesForRouteDistances() {
        // given
        // flights are pre-loaded in FlightRepository

        // when
        ResponseEntity<List<Flight>> response = restTemplate.exchange(
                FLIGHTS_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        List<String> widebodyAircraft = List.of(
                "Boeing 777-300ER", "Boeing 777-200", "Boeing 787-9", "Boeing 787-8", "Boeing 787-10",
                "Boeing 747-8", "Airbus A330-900neo", "Airbus A340-600", "Airbus A340-300",
                "Airbus A350-900", "Airbus A380-800"
        );

        List<String> narrowbodyAircraft = List.of(
                "Boeing 737-800", "Boeing 737 MAX 9", "Airbus A320", "Airbus A320neo", "Airbus A321neo",
                "Embraer E175", "Embraer E190", "Embraer E195"
        );

        response.getBody().forEach(flight -> {
            if (flight.flightDuration().toHours() >= 6) {
                assertThat(widebodyAircraft)
                        .as("Long-haul flight %s should use widebody aircraft", flight.flightNumber())
                        .contains(flight.aircraftType());
            }
        });

        response.getBody().stream()
                .filter(f -> f.flightDuration().toHours() <= 3)
                .forEach(flight ->
                        assertThat(narrowbodyAircraft)
                                .as("Short-haul flight %s should use narrowbody aircraft", flight.flightNumber())
                                .contains(flight.aircraftType())
                );
    }

    private void assertFlight(Flight flight,
                              String expectedFlightNumber, String expectedAirlineCode, String expectedAirlineName,
                              String expectedDepartureAirportCode, String expectedDepartureAirportName, String expectedDepartureCity,
                              String expectedArrivalAirportCode, String expectedArrivalAirportName, String expectedArrivalCity,
                              LocalTime expectedDepartureTime, LocalTime expectedArrivalTime, Duration expectedFlightDuration,
                              String expectedAircraftType, BigDecimal expectedPriceUsd, int expectedAvailableSeats) {
        assertThat(flight.flightNumber()).isEqualTo(expectedFlightNumber);
        assertThat(flight.airlineCode()).isEqualTo(expectedAirlineCode);
        assertThat(flight.airlineName()).isEqualTo(expectedAirlineName);
        assertThat(flight.departureAirportCode()).isEqualTo(expectedDepartureAirportCode);
        assertThat(flight.departureAirportName()).isEqualTo(expectedDepartureAirportName);
        assertThat(flight.departureCity()).isEqualTo(expectedDepartureCity);
        assertThat(flight.arrivalAirportCode()).isEqualTo(expectedArrivalAirportCode);
        assertThat(flight.arrivalAirportName()).isEqualTo(expectedArrivalAirportName);
        assertThat(flight.arrivalCity()).isEqualTo(expectedArrivalCity);
        assertThat(flight.departureTime()).isEqualTo(expectedDepartureTime);
        assertThat(flight.arrivalTime()).isEqualTo(expectedArrivalTime);
        assertThat(flight.flightDuration()).isEqualTo(expectedFlightDuration);
        assertThat(flight.aircraftType()).isEqualTo(expectedAircraftType);
        assertThat(flight.priceUsd()).isEqualTo(expectedPriceUsd);
        assertThat(flight.availableSeats()).isEqualTo(expectedAvailableSeats);
    }
}
