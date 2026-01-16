package com.dominikcebula.spring.ai.cars.api.bookings;

import java.time.LocalDate;

public record Driver(
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String driverLicenseNumber,
        String email,
        String phoneNumber
) {
}
