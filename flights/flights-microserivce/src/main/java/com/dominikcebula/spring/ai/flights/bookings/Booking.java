package com.dominikcebula.spring.ai.flights.bookings;

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
    public Booking withStatus(BookingStatus newStatus) {
        return new Booking(
                bookingReference, passengers, flightNumbers, travelDate,
                newStatus, totalPrice, createdAt, LocalDateTime.now()
        );
    }

    public Booking withUpdatedDetails(List<Passenger> newPassengers, List<String> newFlightNumbers,
                                      LocalDate newTravelDate, BigDecimal newTotalPrice) {
        return new Booking(
                bookingReference, newPassengers, newFlightNumbers, newTravelDate,
                status, newTotalPrice, createdAt, LocalDateTime.now()
        );
    }
}
