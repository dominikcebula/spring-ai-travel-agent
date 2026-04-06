package com.dominikcebula.spring.ai.cars.api.cars;

import java.math.BigDecimal;

public record Car(
        String carId,
        String locationId,
        CarType carType,
        String brand,
        String model,
        int year,
        String description,
        BigDecimal pricePerDay,
        int seats,
        boolean available
) {
}
