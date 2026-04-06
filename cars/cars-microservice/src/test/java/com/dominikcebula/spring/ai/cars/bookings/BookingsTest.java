package com.dominikcebula.spring.ai.cars.bookings;

import com.dominikcebula.spring.ai.cars.api.bookings.*;
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
    private static final LocalDate DRIVER_DATE_OF_BIRTH = LocalDate.of(1990, 1, 15);
    private static final String DRIVER_PHONE = "+1-555-0100";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldCreateBookingWithSingleDriver() {
        // given
        Driver driver = createDriver("John", "Doe", "DL123456");
        LocalDate pickUp = LocalDate.of(2025, 6, 15);
        LocalDate returnDate = LocalDate.of(2025, 6, 18);
        CreateBookingRequest request = new CreateBookingRequest(
                "LOC-WAW-001",
                "CAR-WAW-001-ECO-01",
                List.of(driver),
                pickUp,
                returnDate
        );

        // when
        ResponseEntity<Booking> response = restTemplate.postForEntity(BOOKINGS_URL, request, Booking.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        Booking booking = response.getBody();
        assertThat(booking.bookingReference()).isNotBlank().hasSize(8);
        assertThat(booking.locationId()).isEqualTo("LOC-WAW-001");
        assertThat(booking.carId()).isEqualTo("CAR-WAW-001-ECO-01");
        assertThat(booking.pickUpDate()).isEqualTo(pickUp);
        assertThat(booking.returnDate()).isEqualTo(returnDate);
        assertThat(booking.status()).isEqualTo(BookingStatus.CONFIRMED);
        assertThat(booking.totalPrice()).isEqualTo(new BigDecimal("25.00").multiply(BigDecimal.valueOf(3)));
        assertThat(booking.createdAt()).isNotNull();
        assertThat(booking.updatedAt()).isNotNull();

        assertThat(booking.drivers()).hasSize(1);
        assertDriver(booking.drivers().getFirst(), "John", "Doe", "DL123456");
    }

    @Test
    void shouldCreateBookingWithMultipleDrivers() {
        // given
        Driver driver1 = createDriver("John", "Doe", "DL123456");
        Driver driver2 = createDriver("Jane", "Smith", "DL789012");
        LocalDate pickUp = LocalDate.of(2025, 7, 20);
        LocalDate returnDate = LocalDate.of(2025, 7, 25);
        CreateBookingRequest request = new CreateBookingRequest(
                "LOC-WAW-001",
                "CAR-WAW-001-CMP-01",
                List.of(driver1, driver2),
                pickUp,
                returnDate
        );

        // when
        ResponseEntity<Booking> response = restTemplate.postForEntity(BOOKINGS_URL, request, Booking.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        Booking booking = response.getBody();
        assertThat(booking.bookingReference()).isNotBlank().hasSize(8);
        assertThat(booking.drivers()).hasSize(2);
        assertDriver(booking.drivers().get(0), "John", "Doe", "DL123456");
        assertDriver(booking.drivers().get(1), "Jane", "Smith", "DL789012");
    }

    @Test
    void shouldCalculateTotalPriceBasedOnNumberOfDays() {
        // given
        Driver driver = createDriver("Test", "User", "DL123456");
        LocalDate pickUp = LocalDate.of(2025, 6, 10);
        LocalDate returnDate = LocalDate.of(2025, 6, 17); // 7 days
        CreateBookingRequest request = new CreateBookingRequest(
                "LOC-WAW-001",
                "CAR-WAW-001-MID-01",
                List.of(driver),
                pickUp,
                returnDate
        );

        // when
        ResponseEntity<Booking> response = restTemplate.postForEntity(BOOKINGS_URL, request, Booking.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();

        Booking booking = response.getBody();
        BigDecimal expectedPrice = new BigDecimal("45.00").multiply(BigDecimal.valueOf(7));
        assertThat(booking.totalPrice()).isEqualTo(expectedPrice);
    }

    @Test
    void shouldRetrieveBookingByReference() {
        // given
        Driver driver = createDriver("Test", "User", "DL123456");
        Booking createdBooking = createTestBooking(driver, "LOC-WAW-002", "CAR-WAW-002-ECO-01");

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
        assertThat(booking.locationId()).isEqualTo("LOC-WAW-002");
        assertThat(booking.status()).isEqualTo(BookingStatus.CONFIRMED);

        assertThat(booking.drivers()).hasSize(1);
        assertDriver(booking.drivers().getFirst(), "Test", "User", "DL123456");
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
        Driver driver1 = createDriver("First", "Driver", "FD111111");
        Driver driver2 = createDriver("Second", "Driver", "SD222222");
        Booking booking1 = createTestBooking(driver1, "LOC-WAW-002", "CAR-WAW-002-CMP-01");
        Booking booking2 = createTestBooking(driver2, "LOC-WAW-002", "CAR-WAW-002-MID-01");

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
    void shouldListBookingsByLocationId() {
        // given
        Driver driver = createDriver("Location", "Test", "LT123456");
        Booking booking = createTestBooking(driver, "LOC-KRK-001", "CAR-KRK-001-ECO-01");

        // when
        ResponseEntity<List<Booking>> response = restTemplate.exchange(
                BOOKINGS_URL + "?locationId=LOC-KRK-001",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).allSatisfy(b -> {
            assertThat(b.locationId()).isEqualTo("LOC-KRK-001");
        });
    }

    @Test
    void shouldUpdateBookingDrivers() {
        // given
        Driver originalDriver = createDriver("Original", "Driver", "OD111111");
        Booking existingBooking = createTestBooking(originalDriver, "LOC-KRK-001", "CAR-KRK-001-CMP-01");

        Driver updatedDriver = createDriver("Updated", "Name", "XY999888");
        UpdateBookingRequest updateRequest = new UpdateBookingRequest(
                List.of(updatedDriver),
                existingBooking.pickUpDate(),
                existingBooking.returnDate()
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

        assertThat(booking.drivers()).hasSize(1);
        assertDriver(booking.drivers().getFirst(), "Updated", "Name", "XY999888");
    }

    @Test
    void shouldUpdateBookingDates() {
        // given
        Driver driver = createDriver("Test", "User", "DL123456");
        Booking existingBooking = createTestBooking(driver, "LOC-KRK-001", "CAR-KRK-001-MID-01");

        LocalDate newPickUp = LocalDate.of(2025, 12, 20);
        LocalDate newReturn = LocalDate.of(2025, 12, 27);
        UpdateBookingRequest updateRequest = new UpdateBookingRequest(
                existingBooking.drivers(),
                newPickUp,
                newReturn
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
        assertThat(booking.pickUpDate()).isEqualTo(newPickUp);
        assertThat(booking.returnDate()).isEqualTo(newReturn);
        assertThat(booking.totalPrice()).isEqualTo(new BigDecimal("40.00").multiply(BigDecimal.valueOf(7)));
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentBooking() {
        // given
        UpdateBookingRequest updateRequest = new UpdateBookingRequest(
                List.of(createDriver("John", "Doe", "DL123456")),
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
        Driver driver = createDriver("Cancel", "Test", "CT123456");
        Booking existingBooking = createTestBooking(driver, "LOC-KRK-001", "CAR-KRK-001-FUL-01");

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

        assertDriver(booking.drivers().getFirst(), "Cancel", "Test", "CT123456");
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
        Driver driver = createDriver("Cancelled", "Booking", "CB123456");
        Booking existingBooking = createTestBooking(driver, "LOC-KRK-001", "CAR-KRK-001-SUV-01");

        restTemplate.exchange(
                BOOKINGS_URL + "/" + existingBooking.bookingReference(),
                HttpMethod.DELETE,
                null,
                Booking.class
        );

        UpdateBookingRequest updateRequest = new UpdateBookingRequest(
                List.of(createDriver("Updated", "Name", "XY999888")),
                existingBooking.pickUpDate(),
                existingBooking.returnDate()
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
        Driver driver = createDriver("Double", "Cancel", "DC123456");
        Booking existingBooking = createTestBooking(driver, "LOC-KRK-001", "CAR-KRK-001-SUV-02");

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
        Driver originalDriver = createDriver("Original", "Data", "OD123456");
        Booking existingBooking = createTestBooking(originalDriver, "LOC-KRK-002", "CAR-KRK-002-ECO-01");
        String originalReference = existingBooking.bookingReference();

        Driver updatedDriver = createDriver("Updated", "Data", "UD789012");
        LocalDate newPickUp = LocalDate.of(2025, 8, 10);
        LocalDate newReturn = LocalDate.of(2025, 8, 15);
        UpdateBookingRequest updateRequest = new UpdateBookingRequest(
                List.of(updatedDriver),
                newPickUp,
                newReturn
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
        assertThat(booking.pickUpDate()).isEqualTo(newPickUp);
        assertThat(booking.returnDate()).isEqualTo(newReturn);
        assertThat(booking.createdAt()).isEqualTo(existingBooking.createdAt());
        assertThat(booking.updatedAt()).isAfter(existingBooking.createdAt());

        assertDriver(booking.drivers().getFirst(), "Updated", "Data", "UD789012");
    }

    private Booking createTestBooking(Driver driver, String locationId, String carId) {
        LocalDate pickUp = LocalDate.of(2025, 6, 15);
        LocalDate returnDate = LocalDate.of(2025, 6, 18);
        CreateBookingRequest request = new CreateBookingRequest(
                locationId,
                carId,
                List.of(driver),
                pickUp,
                returnDate
        );

        ResponseEntity<Booking> response = restTemplate.postForEntity(BOOKINGS_URL, request, Booking.class);
        return response.getBody();
    }

    private Driver createDriver(String firstName, String lastName, String driverLicenseNumber) {
        return new Driver(
                firstName,
                lastName,
                DRIVER_DATE_OF_BIRTH,
                driverLicenseNumber,
                firstName.toLowerCase() + "." + lastName.toLowerCase() + "@example.com",
                DRIVER_PHONE
        );
    }

    private void assertDriver(Driver driver, String expectedFirstName, String expectedLastName,
                              String expectedDriverLicenseNumber) {
        assertThat(driver.firstName()).isEqualTo(expectedFirstName);
        assertThat(driver.lastName()).isEqualTo(expectedLastName);
        assertThat(driver.dateOfBirth()).isEqualTo(DRIVER_DATE_OF_BIRTH);
        assertThat(driver.driverLicenseNumber()).isEqualTo(expectedDriverLicenseNumber);
        assertThat(driver.email()).isEqualTo(
                expectedFirstName.toLowerCase() + "." + expectedLastName.toLowerCase() + "@example.com"
        );
        assertThat(driver.phoneNumber()).isEqualTo(DRIVER_PHONE);
    }
}
