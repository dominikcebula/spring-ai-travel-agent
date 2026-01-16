package com.dominikcebula.spring.ai.hotels.bookings;

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

    public List<Booking> findByHotelId(String hotelId) {
        return bookings.values().stream()
                .filter(booking -> booking.hotelId().equals(hotelId))
                .toList();
    }

    public Booking save(Booking booking) {
        bookings.put(booking.bookingReference(), booking);
        return booking;
    }
}
