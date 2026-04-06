package com.dominikcebula.spring.ai.cars.api.bookings;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record Booking(
        String bookingReference,
        String locationId,
        String carId,
        List<Driver> drivers,
        LocalDate pickUpDate,
        LocalDate returnDate,
        BookingStatus status,
        BigDecimal totalPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
