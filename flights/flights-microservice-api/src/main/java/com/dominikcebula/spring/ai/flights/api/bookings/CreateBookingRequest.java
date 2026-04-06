package com.dominikcebula.spring.ai.flights.api.bookings;

import java.time.LocalDate;
import java.util.List;

public record CreateBookingRequest(
        List<Passenger> passengers,
        List<String> flightNumbers,
        LocalDate travelDate
) {
}
