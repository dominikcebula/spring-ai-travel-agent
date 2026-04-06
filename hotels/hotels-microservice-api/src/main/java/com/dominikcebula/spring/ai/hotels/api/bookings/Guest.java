package com.dominikcebula.spring.ai.hotels.api.bookings;

import java.time.LocalDate;

public record Guest(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String passportNumber,
        String email,
        String phoneNumber
) {
}
