package com.dominikcebula.spring.ai.flights.flights;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        // flights are pre-loaded in FlightsRepository

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
    void shouldReturnFlightsWithIataStyleData() {
        // given
        // flights are pre-loaded in FlightsRepository

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
            assertThat(flight.departureAirportCode()).hasSize(3);
            assertThat(flight.arrivalAirportCode()).hasSize(3);
            assertThat(flight.aircraftType()).isNotBlank();
            assertThat(flight.priceUsd()).isPositive();
        });
    }

    @Test
    void shouldRetrieveFlightByFlightNumber() {
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
        assertThat(response.getBody().flightNumber()).isEqualTo("AA100");
        assertThat(response.getBody().airlineCode()).isEqualTo("AA");
        assertThat(response.getBody().airlineName()).isEqualTo("American Airlines");
        assertThat(response.getBody().departureAirportCode()).isEqualTo("JFK");
        assertThat(response.getBody().arrivalAirportCode()).isEqualTo("LHR");
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
    void shouldFilterFlightsByDepartureAirport() {
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
        assertThat(response.getBody()).allSatisfy(flight ->
                assertThat(flight.departureAirportCode()).isEqualTo("JFK")
        );
    }

    @Test
    void shouldFilterFlightsByArrivalAirport() {
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
        assertThat(response.getBody()).allSatisfy(flight ->
                assertThat(flight.arrivalAirportCode()).isEqualTo("WAW")
        );
    }

    @Test
    void shouldFilterFlightsByRoute() {
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
            assertThat(flight.arrivalAirportCode()).isEqualTo("LHR");
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
    void shouldContainTransatlanticRoutes() {
        // given
        // flights are pre-loaded in FlightsRepository

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

        assertThat(response.getBody())
                .anyMatch(flight ->
                        usAirports.contains(flight.departureAirportCode()) &&
                                euAirports.contains(flight.arrivalAirportCode())
                );

        assertThat(response.getBody())
                .anyMatch(flight ->
                        euAirports.contains(flight.departureAirportCode()) &&
                                usAirports.contains(flight.arrivalAirportCode())
                );
    }

    @Test
    void shouldContainFlightsFromMajorAirlines() {
        // given
        // flights are pre-loaded in FlightsRepository

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

        List<String> expectedAirlines = List.of("AA", "UA", "DL", "BA", "LH", "KL", "LO");
        assertThat(response.getBody())
                .extracting(Flight::airlineCode)
                .containsAnyElementsOf(expectedAirlines);
    }

    @Test
    void shouldReturnFlightsWithValidDurationAndTimes() {
        // given
        // flights are pre-loaded in FlightsRepository

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
            assertThat(flight.departureTime()).isNotNull();
            assertThat(flight.arrivalTime()).isNotNull();
            assertThat(flight.flightDuration()).isNotNull();
            assertThat(flight.flightDuration().toMinutes()).isPositive();
        });
    }

    @Test
    void shouldReturnFlightsWithAvailableSeats() {
        // given
        // flights are pre-loaded in FlightsRepository

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
        assertThat(response.getBody()).allSatisfy(flight ->
                assertThat(flight.availableSeats()).isPositive()
        );
    }

    @Test
    void shouldContainLotPolishAirlinesFlightsToWarsaw() {
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
        assertThat(response.getBody())
                .anyMatch(flight ->
                        flight.airlineCode().equals("LO") &&
                                flight.airlineName().equals("LOT Polish Airlines")
                );
    }
}
