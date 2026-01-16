package com.dominikcebula.spring.ai.cars.api.cars;

public record Location(
        String locationId,
        String airportCode,
        String cityName,
        String address
) {
}
