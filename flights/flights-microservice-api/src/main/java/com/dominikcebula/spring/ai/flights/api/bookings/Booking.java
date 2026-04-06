package com.dominikcebula.spring.ai.flights.api.bookings;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record Booking(
        String bookingReference,
        List<Passenger> passengers,
        List<String> flightNumbers,
        LocalDate travelDate,
        BookingStatus status,
        BigDecimal totalPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
