package com.dominikcebula.spring.ai.cars.cars;

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
    public Car withAvailability(boolean newAvailable) {
        return new Car(carId, locationId, carType, brand, model, year, description, pricePerDay, seats, newAvailable);
    }
}
