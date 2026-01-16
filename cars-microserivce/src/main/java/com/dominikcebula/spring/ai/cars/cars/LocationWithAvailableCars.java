package com.dominikcebula.spring.ai.cars.cars;

import java.util.List;

public record LocationWithAvailableCars(
        Location location,
        List<Car> availableCars
) {
}
