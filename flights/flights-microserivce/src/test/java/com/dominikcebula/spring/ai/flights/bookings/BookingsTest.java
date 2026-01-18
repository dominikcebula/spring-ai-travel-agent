package com.dominikcebula.spring.ai.flights.bookings;

import com.dominikcebula.spring.ai.flights.api.bookings.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class BookingsTest {

    private static final String BOOKINGS_URL = "/api/v1/bookings";
    private static final LocalDate PASSENGER_DATE_OF_BIRTH = LocalDate.of(1990, 1, 15);
    private static final String PASSENGER_PHONE = "+1-555-0100";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateBookingWithSinglePassengerAndSingleFlight() {
        // given
        Passenger passenger = createPassenger("John", "Doe", "AB123456");
        LocalDate travelDate = LocalDate.of(2025, 6, 15);
        CreateBookingRequest request = new CreateBookingRequest(
                List.of(passenger),
                List.of("AA100"),
                travelDate
        );

        // when
        ResponseEntity<Booking> response = restTemplate.postForEntity(BOOKINGS_URL, request, Booking.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        Booking booking = response.getBody();
        assertThat(booking.bookingReference()).isNotBlank().hasSize(8);
        assertBooking(booking, List.of("AA100"), travelDate, BookingStatus.CONFIRMED, new BigDecimal("856.00"));
        assertThat(booking.createdAt()).isNotNull();
        assertThat(booking.updatedAt()).isNotNull();

        assertThat(booking.passengers()).hasSize(1);
        assertPassenger(booking.passengers().getFirst(), "John", "Doe", "AB123456");
    }

    @Test
    void shouldCreateBookingWithMultiplePassengersAndMultipleFlights() {
        // given
        Passenger passenger1 = createPassenger("John", "Doe", "AB123456");
        Passenger passenger2 = createPassenger("Jane", "Smith", "CD789012");
        LocalDate travelDate = LocalDate.of(2025, 7, 20);
        CreateBookingRequest request = new CreateBookingRequest(
                List.of(passenger1, passenger2),
                List.of("AA100", "BA117"),
                travelDate
        );

        // when
        ResponseEntity<Booking> response = restTemplate.postForEntity(BOOKINGS_URL, request, Booking.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        Booking booking = response.getBody();
        assertThat(booking.bookingReference()).isNotBlank().hasSize(8);
        assertBooking(booking, List.of("AA100", "BA117"), travelDate, BookingStatus.CONFIRMED);

        assertThat(booking.passengers()).hasSize(2);
        assertPassenger(booking.passengers().get(0), "John", "Doe", "AB123456");
        assertPassenger(booking.passengers().get(1), "Jane", "Smith", "CD789012");
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

        Booking booking = response.getBody();
        BigDecimal expectedPrice = new BigDecimal("856.00").multiply(BigDecimal.valueOf(2));
        assertThat(booking.totalPrice()).isEqualTo(expectedPrice);
    }

    @Test
    void shouldRetrieveBookingByReference() {
        // given
        Passenger passenger = createPassenger("Test", "User", "TE123456");
        LocalDate travelDate = LocalDate.of(2025, 6, 15);
        Booking createdBooking = createTestBooking(passenger, List.of("AA100"), travelDate);

        // when
        ResponseEntity<Booking> response = restTemplate.getForEntity(
                BOOKINGS_URL + "/" + createdBooking.bookingReference(),
                Booking.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Booking booking = response.getBody();
        assertThat(booking.bookingReference()).isEqualTo(createdBooking.bookingReference());
        assertBooking(booking, List.of("AA100"), travelDate, BookingStatus.CONFIRMED, new BigDecimal("856.00"));
        assertThat(booking.createdAt()).isEqualTo(createdBooking.createdAt());

        assertThat(booking.passengers()).hasSize(1);
        assertPassenger(booking.passengers().getFirst(), "Test", "User", "TE123456");
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
        Passenger passenger1 = createPassenger("First", "Passenger", "FP111111");
        Passenger passenger2 = createPassenger("Second", "Passenger", "SP222222");
        Booking booking1 = createTestBooking(passenger1, List.of("AA100"), LocalDate.of(2025, 6, 15));
        Booking booking2 = createTestBooking(passenger2, List.of("BA117"), LocalDate.of(2025, 7, 20));

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
        assertThat(response.getBody()).hasSizeGreaterThanOrEqualTo(2);

        assertThat(response.getBody())
                .extracting(Booking::bookingReference)
                .contains(booking1.bookingReference(), booking2.bookingReference());

        assertThat(response.getBody())
                .filteredOn(b -> b.bookingReference().equals(booking1.bookingReference()))
                .singleElement()
                .satisfies(b -> {
                    assertPassenger(b.passengers().getFirst(), "First", "Passenger", "FP111111");
                    assertThat(b.flightNumbers()).containsExactly("AA100");
                });

        assertThat(response.getBody())
                .filteredOn(b -> b.bookingReference().equals(booking2.bookingReference()))
                .singleElement()
                .satisfies(b -> {
                    assertPassenger(b.passengers().getFirst(), "Second", "Passenger", "SP222222");
                    assertThat(b.flightNumbers()).containsExactly("BA117");
                });
    }

    @Test
    void shouldUpdateBookingPassengers() {
        // given
        Passenger originalPassenger = createPassenger("Original", "Passenger", "OR111111");
        LocalDate travelDate = LocalDate.of(2025, 6, 15);
        Booking existingBooking = createTestBooking(originalPassenger, List.of("AA100"), travelDate);

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

        Booking booking = response.getBody();
        assertThat(booking.bookingReference()).isEqualTo(existingBooking.bookingReference());
        assertBooking(booking, List.of("AA100"), travelDate, BookingStatus.CONFIRMED);
        assertThat(booking.createdAt()).isEqualTo(existingBooking.createdAt());
        assertThat(booking.updatedAt()).isAfter(existingBooking.createdAt());

        assertThat(booking.passengers()).hasSize(1);
        assertPassenger(booking.passengers().getFirst(), "Updated", "Name", "XY999888");
    }

    @Test
    void shouldUpdateBookingFlights() {
        // given
        Passenger passenger = createPassenger("Test", "User", "TE123456");
        LocalDate travelDate = LocalDate.of(2025, 6, 15);
        Booking existingBooking = createTestBooking(passenger, List.of("AA100"), travelDate);
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

        Booking booking = response.getBody();
        assertThat(booking.bookingReference()).isEqualTo(existingBooking.bookingReference());
        assertBooking(booking, List.of("UA900"), travelDate, BookingStatus.CONFIRMED, new BigDecimal("742.00"));
        assertThat(booking.createdAt()).isEqualTo(existingBooking.createdAt());
        assertThat(booking.updatedAt()).isAfter(existingBooking.createdAt());

        assertPassenger(booking.passengers().getFirst(), "Test", "User", "TE123456");
    }

    @Test
    void shouldUpdateBookingTravelDate() {
        // given
        Passenger passenger = createPassenger("Test", "User", "TE123456");
        LocalDate originalDate = LocalDate.of(2025, 6, 15);
        LocalDate newTravelDate = LocalDate.of(2025, 12, 25);
        Booking existingBooking = createTestBooking(passenger, List.of("AA100"), originalDate);

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

        Booking booking = response.getBody();
        assertThat(booking.bookingReference()).isEqualTo(existingBooking.bookingReference());
        assertBooking(booking, List.of("AA100"), newTravelDate, BookingStatus.CONFIRMED, new BigDecimal("856.00"));
        assertThat(booking.createdAt()).isEqualTo(existingBooking.createdAt());
        assertThat(booking.updatedAt()).isAfter(existingBooking.createdAt());
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
        Passenger passenger = createPassenger("Cancel", "Test", "CT123456");
        LocalDate travelDate = LocalDate.of(2025, 6, 15);
        Booking existingBooking = createTestBooking(passenger, List.of("AA100"), travelDate);

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

        Booking booking = response.getBody();
        assertThat(booking.bookingReference()).isEqualTo(existingBooking.bookingReference());
        assertBooking(booking, List.of("AA100"), travelDate, BookingStatus.CANCELLED, new BigDecimal("856.00"));
        assertThat(booking.createdAt()).isEqualTo(existingBooking.createdAt());
        assertThat(booking.updatedAt()).isAfterOrEqualTo(existingBooking.createdAt());

        assertPassenger(booking.passengers().getFirst(), "Cancel", "Test", "CT123456");
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
        Passenger passenger = createPassenger("Cancelled", "Booking", "CB123456");
        Booking existingBooking = createTestBooking(passenger, List.of("AA100"), LocalDate.of(2025, 6, 15));

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
        Passenger passenger = createPassenger("Double", "Cancel", "DC123456");
        Booking existingBooking = createTestBooking(passenger, List.of("AA100"), LocalDate.of(2025, 6, 15));

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
        Passenger originalPassenger = createPassenger("Original", "Data", "OD123456");
        LocalDate originalDate = LocalDate.of(2025, 6, 15);
        Booking existingBooking = createTestBooking(originalPassenger, List.of("AA100"), originalDate);
        String originalReference = existingBooking.bookingReference();

        Passenger updatedPassenger = createPassenger("Updated", "Data", "UD789012");
        LocalDate newTravelDate = LocalDate.of(2025, 8, 10);
        UpdateBookingRequest updateRequest = new UpdateBookingRequest(
                List.of(updatedPassenger),
                List.of("DL40"),
                newTravelDate
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

        Booking booking = response.getBody();
        assertThat(booking.bookingReference()).isEqualTo(originalReference);
        assertBooking(booking, List.of("DL40"), newTravelDate, BookingStatus.CONFIRMED, new BigDecimal("698.00"));
        assertThat(booking.createdAt()).isEqualTo(existingBooking.createdAt());
        assertThat(booking.updatedAt()).isAfter(existingBooking.createdAt());

        assertPassenger(booking.passengers().getFirst(), "Updated", "Data", "UD789012");
    }

    @Test
    void shouldRecalculatePriceWhenUpdatingFlightsAndPassengers() {
        // given
        Passenger singlePassenger = createPassenger("Single", "Passenger", "SP123456");
        Booking existingBooking = createTestBooking(singlePassenger, List.of("AA100"), LocalDate.of(2025, 6, 15));

        Passenger passenger1 = createPassenger("First", "Updated", "FU111111");
        Passenger passenger2 = createPassenger("Second", "Updated", "SU222222");
        UpdateBookingRequest updateRequest = new UpdateBookingRequest(
                List.of(passenger1, passenger2),
                List.of("AA100", "BA117"),
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

        Booking booking = response.getBody();
        BigDecimal aa100Price = new BigDecimal("856.00");
        BigDecimal ba117Price = new BigDecimal("876.00");
        BigDecimal expectedTotal = aa100Price.add(ba117Price).multiply(BigDecimal.valueOf(2));
        assertThat(booking.totalPrice()).isEqualTo(expectedTotal);

        assertThat(booking.passengers()).hasSize(2);
        assertPassenger(booking.passengers().get(0), "First", "Updated", "FU111111");
        assertPassenger(booking.passengers().get(1), "Second", "Updated", "SU222222");
    }

    private Booking createTestBooking(Passenger passenger, List<String> flightNumbers, LocalDate travelDate) {
        CreateBookingRequest request = new CreateBookingRequest(
                List.of(passenger),
                flightNumbers,
                travelDate
        );

        ResponseEntity<Booking> response = restTemplate.postForEntity(BOOKINGS_URL, request, Booking.class);
        return response.getBody();
    }

    private Passenger createPassenger(String firstName, String lastName, String passportNumber) {
        return new Passenger(
                firstName,
                lastName,
                PASSENGER_DATE_OF_BIRTH,
                passportNumber,
                firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com",
                PASSENGER_PHONE
        );
    }

    private void assertPassenger(Passenger passenger, String expectedFirstName, String expectedLastName,
                                 String expectedPassportNumber) {
        assertThat(passenger.firstName()).isEqualTo(expectedFirstName);
        assertThat(passenger.lastName()).isEqualTo(expectedLastName);
        assertThat(passenger.dateOfBirth()).isEqualTo(PASSENGER_DATE_OF_BIRTH);
        assertThat(passenger.passportNumber()).isEqualTo(expectedPassportNumber);
        assertThat(passenger.email()).isEqualTo(
                expectedFirstName.toLowerCase() + "." + expectedLastName.toLowerCase() + "@example.com"
        );
        assertThat(passenger.phoneNumber()).isEqualTo(PASSENGER_PHONE);
    }

    private void assertBooking(Booking booking, List<String> expectedFlightNumbers, LocalDate expectedTravelDate,
                               BookingStatus expectedStatus) {
        assertThat(booking.flightNumbers()).containsExactlyElementsOf(expectedFlightNumbers);
        assertThat(booking.travelDate()).isEqualTo(expectedTravelDate);
        assertThat(booking.status()).isEqualTo(expectedStatus);
    }

    private void assertBooking(Booking booking, List<String> expectedFlightNumbers, LocalDate expectedTravelDate,
                               BookingStatus expectedStatus, BigDecimal expectedTotalPrice) {
        assertBooking(booking, expectedFlightNumbers, expectedTravelDate, expectedStatus);
        assertThat(booking.totalPrice()).isEqualTo(expectedTotalPrice);
    }
}
