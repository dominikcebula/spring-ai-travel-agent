package com.dominikcebula.spring.ai.cars.cars;

import com.dominikcebula.spring.ai.cars.api.cars.Car;

public class CarFactory {
    private final Car car;

    public CarFactory(Car car) {
        this.car = car;
    }

    public Car withAvailability(boolean newAvailable) {
        return new Car(car.carId(), car.locationId(), car.carType(), car.brand(), car.model(), car.year(), car.description(), car.pricePerDay(), car.seats(), newAvailable);
    }
}
