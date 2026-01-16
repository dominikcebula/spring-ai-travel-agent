package com.dominikcebula.spring.ai.flights.bookings;

import java.time.LocalDate;
import java.util.List;

public record UpdateBookingRequest(
        List<Passenger> passengers,
        List<String> flightNumbers,
        LocalDate travelDate
) {
}
