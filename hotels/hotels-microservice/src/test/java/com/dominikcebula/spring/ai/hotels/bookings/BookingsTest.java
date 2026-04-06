package com.dominikcebula.spring.ai.hotels.bookings;

import com.dominikcebula.spring.ai.hotels.api.bookings.*;
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
    private static final LocalDate GUEST_DATE_OF_BIRTH = LocalDate.of(1990, 1, 15);
    private static final String GUEST_PHONE = "+1-555-0100";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateBookingWithSingleGuest() {
        // given
        Guest guest = createGuest("John", "Doe", "AB123456");
        LocalDate checkIn = LocalDate.of(2025, 6, 15);
        LocalDate checkOut = LocalDate.of(2025, 6, 18);
        CreateBookingRequest request = new CreateBookingRequest(
                "HTL-WAW-001",
                "WAW-001-SGL-101",
                List.of(guest),
                checkIn,
                checkOut
        );

        // when
        ResponseEntity<Booking> response = restTemplate.postForEntity(BOOKINGS_URL, request, Booking.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        Booking booking = response.getBody();
        assertThat(booking.bookingReference()).isNotBlank().hasSize(8);
        assertThat(booking.hotelId()).isEqualTo("HTL-WAW-001");
        assertThat(booking.roomId()).isEqualTo("WAW-001-SGL-101");
        assertThat(booking.checkInDate()).isEqualTo(checkIn);
        assertThat(booking.checkOutDate()).isEqualTo(checkOut);
        assertThat(booking.status()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(booking.totalPrice()).isEqualTo(new BigDecimal("180.00").multiply(BigDecimal.valueOf(3)));
        assertThat(booking.createdAt()).isNotNull();
        assertThat(booking.updatedAt()).isNotNull();

        assertThat(booking.guests()).hasSize(1);
        assertGuest(booking.guests().getFirst(), "John", "Doe", "AB123456");
    }

    @Test
    void shouldCreateBookingWithMultipleGuests() {
        // given
        Guest guest1 = createGuest("John", "Doe", "AB123456");
        Guest guest2 = createGuest("Jane", "Smith", "CD789012");
        LocalDate checkIn = LocalDate.of(2025, 7, 20);
        LocalDate checkOut = LocalDate.of(2025, 7, 25);
        CreateBookingRequest request = new CreateBookingRequest(
                "HTL-WAW-001",
                "WAW-001-DBL-201",
                List.of(guest1, guest2),
                checkIn,
                checkOut
        );

        // when
        ResponseEntity<Booking> response = restTemplate.postForEntity(BOOKINGS_URL, request, Booking.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        Booking booking = response.getBody();
        assertThat(booking.bookingReference()).isNotBlank().hasSize(8);
        assertThat(booking.guests()).hasSize(2);
        assertGuest(booking.guests().get(0), "John", "Doe", "AB123456");
        assertGuest(booking.guests().get(1), "Jane", "Smith", "CD789012");
    }

    @Test
    void shouldCalculateTotalPriceBasedOnNumberOfNights() {
        // given
        Guest guest = createGuest("Test", "User", "TE123456");
        LocalDate checkIn = LocalDate.of(2025, 6, 10);
        LocalDate checkOut = LocalDate.of(2025, 6, 15); // 5 nights
        CreateBookingRequest request = new CreateBookingRequest(
                "HTL-WAW-001",
                "WAW-001-STE-601",
                List.of(guest),
                checkIn,
                checkOut
        );

        // when
        ResponseEntity<Booking> response = restTemplate.postForEntity(BOOKINGS_URL, request, Booking.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        Booking booking = response.getBody();
        BigDecimal expectedPrice = new BigDecimal("580.00").multiply(BigDecimal.valueOf(5));
        assertThat(booking.totalPrice()).isEqualTo(expectedPrice);
    }

    @Test
    void shouldRetrieveBookingByReference() {
        // given
        Guest guest = createGuest("Test", "User", "TE123456");
        Booking createdBooking = createTestBooking(guest, "HTL-WAW-002", "WAW-002-SGL-101");

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
        assertThat(booking.hotelId()).isEqualTo("HTL-WAW-002");
        assertThat(booking.status()).isEqualTo(BookingStatus.CONFIRMED);

        assertThat(booking.guests()).hasSize(1);
        assertGuest(booking.guests().getFirst(), "Test", "User", "TE123456");
    }

    @Test
    void shouldReturnNotFoundForNonExistentBooking() {
        // when
        ResponseEntity<Booking> response = restTemplate.getForEntity(
                BOOKINGS_URL + "/NOTEXIST",
                Booking.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldListAllBookings() {
        // given
        Guest guest1 = createGuest("First", "Guest", "FG111111");
        Guest guest2 = createGuest("Second", "Guest", "SG222222");
        Booking booking1 = createTestBooking(guest1, "HTL-WAW-002", "WAW-002-SGL-102");
        Booking booking2 = createTestBooking(guest2, "HTL-WAW-002", "WAW-002-DBL-202");

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
    }

    @Test
    void shouldListBookingsByHotelId() {
        // given
        Guest guest = createGuest("Hotel", "Test", "HT123456");
        Booking booking = createTestBooking(guest, "HTL-KRK-001", "KRK-001-SGL-101");

        // when
        ResponseEntity<List<Booking>> response = restTemplate.exchange(
                BOOKINGS_URL + "?hotelId=HTL-KRK-001",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).allSatisfy(b -> {
            assertThat(b.hotelId()).isEqualTo("HTL-KRK-001");
        });
    }

    @Test
    void shouldUpdateBookingGuests() {
        // given
        Guest originalGuest = createGuest("Original", "Guest", "OG111111");
        Booking existingBooking = createTestBooking(originalGuest, "HTL-KRK-001", "KRK-001-SGL-102");

        Guest updatedGuest = createGuest("Updated", "Name", "XY999888");
        UpdateBookingRequest updateRequest = new UpdateBookingRequest(
                List.of(updatedGuest),
                existingBooking.checkInDate(),
                existingBooking.checkOutDate()
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
        assertThat(booking.createdAt()).isEqualTo(existingBooking.createdAt());
        assertThat(booking.updatedAt()).isAfter(existingBooking.createdAt());

        assertThat(booking.guests()).hasSize(1);
        assertGuest(booking.guests().getFirst(), "Updated", "Name", "XY999888");
    }

    @Test
    void shouldUpdateBookingDates() {
        // given
        Guest guest = createGuest("Test", "User", "TE123456");
        Booking existingBooking = createTestBooking(guest, "HTL-KRK-001", "KRK-001-DBL-201");

        LocalDate newCheckIn = LocalDate.of(2025, 12, 20);
        LocalDate newCheckOut = LocalDate.of(2025, 12, 27);
        UpdateBookingRequest updateRequest = new UpdateBookingRequest(
                existingBooking.guests(),
                newCheckIn,
                newCheckOut
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
        assertThat(booking.checkInDate()).isEqualTo(newCheckIn);
        assertThat(booking.checkOutDate()).isEqualTo(newCheckOut);
        assertThat(booking.totalPrice()).isEqualTo(new BigDecimal("220.00").multiply(BigDecimal.valueOf(7)));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentBooking() {
        // given
        UpdateBookingRequest updateRequest = new UpdateBookingRequest(
                List.of(createGuest("John", "Doe", "AB123456")),
                LocalDate.of(2025, 6, 15),
                LocalDate.of(2025, 6, 18)
        );

        // when
        ResponseEntity<Booking> response = restTemplate.exchange(
                BOOKINGS_URL + "/NOTEXIST",
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
        Guest guest = createGuest("Cancel", "Test", "CT123456");
        Booking existingBooking = createTestBooking(guest, "HTL-KRK-001", "KRK-001-TWN-301");

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
        assertThat(booking.status()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(booking.createdAt()).isEqualTo(existingBooking.createdAt());
        assertThat(booking.updatedAt()).isAfterOrEqualTo(existingBooking.createdAt());

        assertGuest(booking.guests().getFirst(), "Cancel", "Test", "CT123456");
    }

    @Test
    void shouldReturnNotFoundWhenCancellingNonExistentBooking() {
        // when
        ResponseEntity<Booking> response = restTemplate.exchange(
                BOOKINGS_URL + "/NOTEXIST",
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
        Guest guest = createGuest("Cancelled", "Booking", "CB123456");
        Booking existingBooking = createTestBooking(guest, "HTL-KRK-001", "KRK-001-DLX-401");

        restTemplate.exchange(
                BOOKINGS_URL + "/" + existingBooking.bookingReference(),
                HttpMethod.DELETE,
                null,
                Booking.class
        );

        UpdateBookingRequest updateRequest = new UpdateBookingRequest(
                List.of(createGuest("Updated", "Name", "XY999888")),
                existingBooking.checkInDate(),
                existingBooking.checkOutDate()
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
        Guest guest = createGuest("Double", "Cancel", "DC123456");
        Booking existingBooking = createTestBooking(guest, "HTL-KRK-001", "KRK-001-FAM-501");

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
        Guest originalGuest = createGuest("Original", "Data", "OD123456");
        Booking existingBooking = createTestBooking(originalGuest, "HTL-KRK-002", "KRK-002-SGL-101");
        String originalReference = existingBooking.bookingReference();

        Guest updatedGuest = createGuest("Updated", "Data", "UD789012");
        LocalDate newCheckIn = LocalDate.of(2025, 8, 10);
        LocalDate newCheckOut = LocalDate.of(2025, 8, 15);
        UpdateBookingRequest updateRequest = new UpdateBookingRequest(
                List.of(updatedGuest),
                newCheckIn,
                newCheckOut
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
        assertThat(booking.checkInDate()).isEqualTo(newCheckIn);
        assertThat(booking.checkOutDate()).isEqualTo(newCheckOut);
        assertThat(booking.createdAt()).isEqualTo(existingBooking.createdAt());
        assertThat(booking.updatedAt()).isAfter(existingBooking.createdAt());

        assertGuest(booking.guests().getFirst(), "Updated", "Data", "UD789012");
    }

    private Booking createTestBooking(Guest guest, String hotelId, String roomId) {
        LocalDate checkIn = LocalDate.of(2025, 6, 15);
        LocalDate checkOut = LocalDate.of(2025, 6, 18);
        CreateBookingRequest request = new CreateBookingRequest(
                hotelId,
                roomId,
                List.of(guest),
                checkIn,
                checkOut
        );

        ResponseEntity<Booking> response = restTemplate.postForEntity(BOOKINGS_URL, request, Booking.class);
        return response.getBody();
    }

    private Guest createGuest(String firstName, String lastName, String passportNumber) {
        return new Guest(
                firstName,
                lastName,
                GUEST_DATE_OF_BIRTH,
                passportNumber,
                firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com",
                GUEST_PHONE
        );
    }

    private void assertGuest(Guest guest, String expectedFirstName, String expectedLastName,
                             String expectedPassportNumber) {
        assertThat(guest.firstName()).isEqualTo(expectedFirstName);
        assertThat(guest.lastName()).isEqualTo(expectedLastName);
        assertThat(guest.dateOfBirth()).isEqualTo(GUEST_DATE_OF_BIRTH);
        assertThat(guest.passportNumber()).isEqualTo(expectedPassportNumber);
        assertThat(guest.email()).isEqualTo(
                expectedFirstName.toLowerCase() + "." + expectedLastName.toLowerCase() + "@example.com"
        );
        assertThat(guest.phoneNumber()).isEqualTo(GUEST_PHONE);
    }
}
