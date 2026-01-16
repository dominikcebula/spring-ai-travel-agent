package com.dominikcebula.spring.ai.cars.api.cars;

import java.util.List;

public record LocationWithAvailableCars(
        Location location,
        List<Car> availableCars
) {
}
