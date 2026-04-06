package com.dominikcebula.spring.ai.hotels.api.bookings;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.*;

import java.util.List;

@HttpExchange("/api/v1/bookings")
public interface BookingsApi {

    @GetExchange
    List<Booking> getAllBookings(@RequestParam(required = false) String hotelId);

    @GetExchange("/{bookingReference}")
    Booking getBooking(@PathVariable String bookingReference);

    @PostExchange
    Booking createBooking(@RequestBody CreateBookingRequest request);

    @PutExchange("/{bookingReference}")
    Booking updateBooking(@PathVariable String bookingReference, @RequestBody UpdateBookingRequest request);

    @DeleteExchange("/{bookingReference}")
    Booking cancelBooking(@PathVariable String bookingReference);
}
