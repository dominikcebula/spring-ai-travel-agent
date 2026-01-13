package com.dominikcebula.spring.ai.flights.bookings;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping
    public List<Booking> getAllBookings() {
        return bookingService.getAllBookings();
    }

    @GetMapping("/{bookingReference}")
    public ResponseEntity<Booking> getBooking(@PathVariable String bookingReference) {
        return bookingService.getBookingByReference(bookingReference)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody CreateBookingRequest request) {
        Booking booking = bookingService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @PutMapping("/{bookingReference}")
    public ResponseEntity<Booking> updateBooking(
            @PathVariable String bookingReference,
            @RequestBody UpdateBookingRequest request) {
        return bookingService.updateBooking(bookingReference, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{bookingReference}")
    public ResponseEntity<Booking> cancelBooking(@PathVariable String bookingReference) {
        return bookingService.cancelBooking(bookingReference)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
