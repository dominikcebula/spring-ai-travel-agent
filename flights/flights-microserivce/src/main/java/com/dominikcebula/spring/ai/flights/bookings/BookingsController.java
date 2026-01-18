package com.dominikcebula.spring.ai.flights.bookings;

import com.dominikcebula.spring.ai.flights.api.bookings.Booking;
import com.dominikcebula.spring.ai.flights.api.bookings.BookingsApi;
import com.dominikcebula.spring.ai.flights.api.bookings.CreateBookingRequest;
import com.dominikcebula.spring.ai.flights.api.bookings.UpdateBookingRequest;
import com.dominikcebula.spring.ai.flights.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BookingsController implements BookingsApi {

    private final BookingsService bookingsService;

    public BookingsController(BookingsService bookingsService) {
        this.bookingsService = bookingsService;
    }

    @Override
    public List<Booking> getAllBookings() {
        return bookingsService.getAllBookings();
    }

    @Override
    public Booking getBooking(String bookingReference) {
        return bookingsService.getBookingByReference(bookingReference)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingReference));
    }

    @Override
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createBooking(CreateBookingRequest request) {
        return bookingsService.createBooking(request);
    }

    @Override
    public Booking updateBooking(String bookingReference, UpdateBookingRequest request) {
        return bookingsService.updateBooking(bookingReference, request)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingReference));
    }

    @Override
    public Booking cancelBooking(String bookingReference) {
        return bookingsService.cancelBooking(bookingReference)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + bookingReference));
    }
}
