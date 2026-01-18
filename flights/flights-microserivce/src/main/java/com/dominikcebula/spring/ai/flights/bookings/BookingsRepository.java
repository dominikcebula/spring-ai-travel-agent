package com.dominikcebula.spring.ai.flights.bookings;

import com.dominikcebula.spring.ai.flights.api.bookings.Booking;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class BookingsRepository {

    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();

    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }

    public Optional<Booking> findByBookingReference(String bookingReference) {
        return Optional.ofNullable(bookings.get(bookingReference));
    }

    public Booking save(Booking booking) {
        bookings.put(booking.bookingReference(), booking);
        return booking;
    }
}
