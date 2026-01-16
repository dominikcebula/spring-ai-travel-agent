package com.dominikcebula.spring.ai.flights.bookings;

import java.time.LocalDate;

public record Passenger(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String passportNumber,
        String email,
        String phoneNumber
) {
}
