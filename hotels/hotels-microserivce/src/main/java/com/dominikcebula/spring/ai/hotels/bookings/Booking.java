package com.dominikcebula.spring.ai.hotels.bookings;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record Booking(
        String bookingReference,
        String hotelId,
        String roomId,
        List<Guest> guests,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        BookingStatus status,
        BigDecimal totalPrice,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public Booking withStatus(BookingStatus newStatus) {
        return new Booking(
                bookingReference, hotelId, roomId, guests, checkInDate, checkOutDate,
                newStatus, totalPrice, createdAt, LocalDateTime.now()
        );
    }

    public Booking withUpdatedDetails(List<Guest> newGuests, LocalDate newCheckInDate,
                                      LocalDate newCheckOutDate, BigDecimal newTotalPrice) {
        return new Booking(
                bookingReference, hotelId, roomId, newGuests, newCheckInDate, newCheckOutDate,
                status, newTotalPrice, createdAt, LocalDateTime.now()
        );
    }
}
