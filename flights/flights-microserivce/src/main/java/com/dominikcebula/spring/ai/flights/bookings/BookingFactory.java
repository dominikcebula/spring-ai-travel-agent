package com.dominikcebula.spring.ai.flights.bookings;

import com.dominikcebula.spring.ai.flights.api.bookings.Booking;
import com.dominikcebula.spring.ai.flights.api.bookings.BookingStatus;
import com.dominikcebula.spring.ai.flights.api.bookings.Passenger;

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
                booking.bookingReference(), booking.passengers(), booking.flightNumbers(), booking.travelDate(),
                newStatus, booking.totalPrice(), booking.createdAt(), LocalDateTime.now()
        );
    }

    public Booking withUpdatedDetails(List<Passenger> newPassengers, List<String> newFlightNumbers,
                                      LocalDate newTravelDate, BigDecimal newTotalPrice) {
        return new Booking(
                booking.bookingReference(), newPassengers, newFlightNumbers, newTravelDate,
                booking.status(), newTotalPrice, booking.createdAt(), LocalDateTime.now()
        );
    }
}
