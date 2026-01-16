package com.dominikcebula.spring.ai.cars.api.bookings;

import java.time.LocalDate;
import java.util.List;

public record UpdateBookingRequest(
        List<Driver> drivers,
        LocalDate pickUpDate,
        LocalDate returnDate
) {
}
