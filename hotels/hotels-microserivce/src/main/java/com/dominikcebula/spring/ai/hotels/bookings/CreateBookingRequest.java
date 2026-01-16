package com.dominikcebula.spring.ai.hotels.bookings;

import java.time.LocalDate;
import java.util.List;

public record CreateBookingRequest(
        String hotelId,
        String roomId,
        List<Guest> guests,
        LocalDate checkInDate,
        LocalDate checkOutDate
) {
}
