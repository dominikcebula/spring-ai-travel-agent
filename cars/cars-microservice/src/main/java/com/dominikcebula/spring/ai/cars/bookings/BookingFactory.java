package com.dominikcebula.spring.ai.cars.bookings;

import com.dominikcebula.spring.ai.cars.api.bookings.Booking;
import com.dominikcebula.spring.ai.cars.api.bookings.BookingStatus;
import com.dominikcebula.spring.ai.cars.api.bookings.Driver;

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
                booking.bookingReference(), booking.locationId(), booking.carId(), booking.drivers(), booking.pickUpDate(), booking.returnDate(),
                newStatus, booking.totalPrice(), booking.createdAt(), LocalDateTime.now()
        );
    }

    public Booking withUpdatedDetails(List<Driver> newDrivers, LocalDate newPickUpDate,
                                      LocalDate newReturnDate, BigDecimal newTotalPrice) {
        return new Booking(
                booking.bookingReference(), booking.locationId(), booking.carId(), newDrivers, newPickUpDate, newReturnDate,
                booking.status(), newTotalPrice, booking.createdAt(), LocalDateTime.now()
        );
    }
}
