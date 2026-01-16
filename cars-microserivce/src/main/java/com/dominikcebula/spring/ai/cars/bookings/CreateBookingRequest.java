package com.dominikcebula.spring.ai.cars.bookings;

import java.time.LocalDate;
import java.util.List;

public record CreateBookingRequest(
        String locationId,
        String carId,
        List<Driver> drivers,
        LocalDate pickUpDate,
        LocalDate returnDate
) {
}
