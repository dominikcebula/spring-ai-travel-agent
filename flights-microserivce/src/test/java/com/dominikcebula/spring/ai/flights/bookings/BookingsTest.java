package com.dominikcebula.spring.ai.flights.bookings;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class BookingsTest {

    private static final String BOOKINGS_URL = "/api/v1/bookings";

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void shouldCreateBookingWithSinglePassengerAndSingleFlight() {
        // given
        Passenger passenger = createPassenger("John", "Doe", "AB123456");
        CreateBookingRequest request = new CreateBookingRequest(
                List.of(passenger),
                List.of("AA100"),
                LocalDate.of(2025, 6, 15)
        );

        // when
        ResponseEntity<Booking> response = restTemplate.postForEntity(BOOKINGS_URL, request, Booking.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().bookingReference()).isNotBlank();
        assertThat(response.getBody().passengers()).hasSize(1);
        assertThat(response.getBody().passengers().getFirst().firstName()).isEqualTo("John");
        assertThat(response.getBody().flightNumbers()).containsExactly("AA100");
        assertThat(response.getBody().status()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(response.getBody().totalPrice()).isNotNull();
        assertThat(response.getBody().createdAt()).isNotNull();
    }

    @Test
    void shouldCreateBookingWithMultiplePassengersAndMultipleFlights() {
        // given
        Passenger passenger1 = createPassenger("John", "Doe", "AB123456");
        Passenger passenger2 = createPassenger("Jane", "Doe", "CD789012");
        CreateBookingRequest request = new CreateBookingRequest(
                List.of(passenger1, passenger2),
                List.of("AA100", "BA117"),
                LocalDate.of(2025, 7, 20)
        );

        // when
        ResponseEntity<Booking> response = restTemplate.postForEntity(BOOKINGS_URL, request, Booking.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().passengers()).hasSize(2);
        assertThat(response.getBody().flightNumbers()).containsExactly("AA100", "BA117");
    }

    @Test
    void shouldCalculateTotalPriceBasedOnPassengerCountAndFlights() {
        // given
        Passenger passenger1 = createPassenger("John", "Doe", "AB123456");
        Passenger passenger2 = createPassenger("Jane", "Doe", "CD789012");
        CreateBookingRequest request = new CreateBookingRequest(
                List.of(passenger1, passenger2),
                List.of("AA100"),
                LocalDate.of(2025, 6, 15)
        );

        // when
        ResponseEntity<Booking> response = restTemplate.postForEntity(BOOKINGS_URL, request, Booking.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().totalPrice()).isPositive();
    }

    @Test
    void shouldRetrieveBookingByReference() {
        // given
        Booking createdBooking = createTestBooking();

        // when
        ResponseEntity<Booking> response = restTemplate.getForEntity(
                BOOKINGS_URL + "/" + createdBooking.bookingReference(),
                Booking.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().bookingReference()).isEqualTo(createdBooking.bookingReference());
        assertThat(response.getBody().passengers()).hasSize(1);
    }

    @Test
    void shouldReturnNotFoundForNonExistentBooking() {
        // given
        String nonExistentReference = "NOTEXIST";

        // when
        ResponseEntity<Booking> response = restTemplate.getForEntity(
                BOOKINGS_URL + "/" + nonExistentReference,
                Booking.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldListAllBookings() {
        // given
        Booking booking1 = createTestBooking();
        Booking booking2 = createTestBooking();

        // when
        ResponseEntity<List<Booking>> response = restTemplate.exchange(
                BOOKINGS_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSizeGreaterThan(0);
        assertThat(response.getBody())
                .extracting(Booking::bookingReference)
                .contains(booking1.bookingReference(), booking2.bookingReference());
    }

    @Test
    void shouldUpdateBookingPassengers() {
        // given
        Booking existingBooking = createTestBooking();
        Passenger updatedPassenger = createPassenger("Updated", "Name", "XY999888");
        UpdateBookingRequest updateRequest = new UpdateBookingRequest(
                List.of(updatedPassenger),
                existingBooking.flightNumbers(),
                existingBooking.travelDate()
        );

        // when
        ResponseEntity<Booking> response = restTemplate.exchange(
                BOOKINGS_URL + "/" + existingBooking.bookingReference(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                Booking.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().passengers()).hasSize(1);
        assertThat(response.getBody().passengers().getFirst().firstName()).isEqualTo("Updated");
        assertThat(response.getBody().updatedAt()).isAfter(existingBooking.createdAt());
    }

    @Test
    void shouldUpdateBookingFlights() {
        // given
        Booking existingBooking = createTestBooking();
        UpdateBookingRequest updateRequest = new UpdateBookingRequest(
                existingBooking.passengers(),
                List.of("UA900"),
                existingBooking.travelDate()
        );

        // when
        ResponseEntity<Booking> response = restTemplate.exchange(
                BOOKINGS_URL + "/" + existingBooking.bookingReference(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                Booking.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().flightNumbers()).containsExactly("UA900");
    }

    @Test
    void shouldUpdateBookingTravelDate() {
        // given
        Booking existingBooking = createTestBooking();
        LocalDate newTravelDate = LocalDate.of(2025, 12, 25);
        UpdateBookingRequest updateRequest = new UpdateBookingRequest(
                existingBooking.passengers(),
                existingBooking.flightNumbers(),
                newTravelDate
        );

        // when
        ResponseEntity<Booking> response = restTemplate.exchange(
                BOOKINGS_URL + "/" + existingBooking.bookingReference(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                Booking.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().travelDate()).isEqualTo(newTravelDate);
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentBooking() {
        // given
        String nonExistentReference = "NOTEXIST";
        UpdateBookingRequest updateRequest = new UpdateBookingRequest(
                List.of(createPassenger("John", "Doe", "AB123456")),
                List.of("AA100"),
                LocalDate.of(2025, 6, 15)
        );

        // when
        ResponseEntity<Booking> response = restTemplate.exchange(
                BOOKINGS_URL + "/" + nonExistentReference,
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                Booking.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldCancelBooking() {
        // given
        Booking existingBooking = createTestBooking();

        // when
        ResponseEntity<Booking> response = restTemplate.exchange(
                BOOKINGS_URL + "/" + existingBooking.bookingReference(),
                HttpMethod.DELETE,
                null,
                Booking.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().status()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(response.getBody().bookingReference()).isEqualTo(existingBooking.bookingReference());
    }

    @Test
    void shouldReturnNotFoundWhenCancellingNonExistentBooking() {
        // given
        String nonExistentReference = "NOTEXIST";

        // when
        ResponseEntity<Booking> response = restTemplate.exchange(
                BOOKINGS_URL + "/" + nonExistentReference,
                HttpMethod.DELETE,
                null,
                Booking.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAllowUpdatingCancelledBooking() {
        // given
        Booking existingBooking = createTestBooking();
        restTemplate.exchange(
                BOOKINGS_URL + "/" + existingBooking.bookingReference(),
                HttpMethod.DELETE,
                null,
                Booking.class
        );

        UpdateBookingRequest updateRequest = new UpdateBookingRequest(
                List.of(createPassenger("Updated", "Name", "XY999888")),
                existingBooking.flightNumbers(),
                existingBooking.travelDate()
        );

        // when
        ResponseEntity<Booking> response = restTemplate.exchange(
                BOOKINGS_URL + "/" + existingBooking.bookingReference(),
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                Booking.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldNotAllowCancellingAlreadyCancelledBooking() {
        // given
        Booking existingBooking = createTestBooking();
        restTemplate.exchange(
                BOOKINGS_URL + "/" + existingBooking.bookingReference(),
                HttpMethod.DELETE,
                null,
                Booking.class
        );

        // when
        ResponseEntity<Booking> response = restTemplate.exchange(
                BOOKINGS_URL + "/" + existingBooking.bookingReference(),
                HttpMethod.DELETE,
                null,
                Booking.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldPreserveBookingReferenceAfterUpdate() {
        // given
        Booking existingBooking = createTestBooking();
        String originalReference = existingBooking.bookingReference();
        UpdateBookingRequest updateRequest = new UpdateBookingRequest(
                List.of(createPassenger("Updated", "Name", "XY999888")),
                List.of("DL40"),
                LocalDate.of(2025, 8, 10)
        );

        // when
        ResponseEntity<Booking> response = restTemplate.exchange(
                BOOKINGS_URL + "/" + originalReference,
                HttpMethod.PUT,
                new HttpEntity<>(updateRequest),
                Booking.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().bookingReference()).isEqualTo(originalReference);
    }

    private Booking createTestBooking() {
        Passenger passenger = createPassenger("Test", "User", "TE123456");
        CreateBookingRequest request = new CreateBookingRequest(
                List.of(passenger),
                List.of("AA100"),
                LocalDate.of(2025, 6, 15)
        );

        ResponseEntity<Booking> response = restTemplate.postForEntity(BOOKINGS_URL, request, Booking.class);
        return response.getBody();
    }

    private Passenger createPassenger(String firstName, String lastName, String passportNumber) {
        return new Passenger(
                firstName,
                lastName,
                LocalDate.of(1990, 1, 15),
                passportNumber,
                firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com",
                "+1-555-0100"
        );
    }
}
