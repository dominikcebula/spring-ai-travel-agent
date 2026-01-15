package com.dominikcebula.spring.ai.hotels.bookings;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingsController {

    private final BookingsService bookingsService;

    public BookingsController(BookingsService bookingsService) {
        this.bookingsService = bookingsService;
    }

    @GetMapping
    public List<Booking> getAllBookings(@RequestParam(required = false) String hotelId) {
        if (hotelId != null) {
            return bookingsService.getBookingsByHotelId(hotelId);
        }
        return bookingsService.getAllBookings();
    }

    @GetMapping("/{bookingReference}")
    public ResponseEntity<Booking> getBooking(@PathVariable String bookingReference) {
        return bookingsService.getBookingByReference(bookingReference)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody CreateBookingRequest request) {
        Booking booking = bookingsService.createBooking(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @PutMapping("/{bookingReference}")
    public ResponseEntity<Booking> updateBooking(
            @PathVariable String bookingReference,
            @RequestBody UpdateBookingRequest request) {
        return bookingsService.updateBooking(bookingReference, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{bookingReference}")
    public ResponseEntity<Booking> cancelBooking(@PathVariable String bookingReference) {
        return bookingsService.cancelBooking(bookingReference)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
