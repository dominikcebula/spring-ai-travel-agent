package com.dominikcebula.spring.ai.hotels.bookings;

import com.dominikcebula.spring.ai.hotels.api.bookings.Booking;
import com.dominikcebula.spring.ai.hotels.api.bookings.BookingStatus;
import com.dominikcebula.spring.ai.hotels.api.bookings.Guest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class BookingFactory {
    private final Booking booking;

    public BookingFactory(Booking booking) {
        this.booking = booking;
    }

    public Booking withStatus(BookingStatus newStatus) {
        return new Booking(
                booking.bookingReference(), booking.hotelId(), booking.roomId(), booking.guests(),
                booking.checkInDate(), booking.checkOutDate(), newStatus, booking.totalPrice(),
                booking.createdAt(), LocalDateTime.now()
        );
    }

    public Booking withUpdatedDetails(List<Guest> newGuests, LocalDate newCheckInDate,
                                      LocalDate newCheckOutDate, BigDecimal newTotalPrice) {
        return new Booking(
                booking.bookingReference(), booking.hotelId(), booking.roomId(), newGuests,
                newCheckInDate, newCheckOutDate, booking.status(), newTotalPrice,
                booking.createdAt(), LocalDateTime.now()
        );
    }
}
