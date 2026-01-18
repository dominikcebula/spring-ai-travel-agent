package com.dominikcebula.spring.ai.hotels.api.bookings;

import java.time.LocalDate;
import java.util.List;

public record UpdateBookingRequest(
        List<Guest> guests,
        LocalDate checkInDate,
        LocalDate checkOutDate
) {
}
