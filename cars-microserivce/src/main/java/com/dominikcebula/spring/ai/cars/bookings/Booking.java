package com.dominikcebula.spring.ai.cars.bookings;

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
    public Booking withStatus(BookingStatus newStatus) {
        return new Booking(
                bookingReference, locationId, carId, drivers, pickUpDate, returnDate,
                newStatus, totalPrice, createdAt, LocalDateTime.now()
        );
    }

    public Booking withUpdatedDetails(List<Driver> newDrivers, LocalDate newPickUpDate,
                                      LocalDate newReturnDate, BigDecimal newTotalPrice) {
        return new Booking(
                bookingReference, locationId, carId, newDrivers, newPickUpDate, newReturnDate,
                status, newTotalPrice, createdAt, LocalDateTime.now()
        );
    }
}
