package com.dominikcebula.spring.ai.cars.api.cars;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("/api/v1/cars")
public interface CarsApi {

    @GetExchange("/locations")
    List<Location> getAllLocations();

    @GetExchange("/locations/{locationId}")
    Location getLocationById(@PathVariable String locationId);

    @GetExchange("/locations/{locationId}/cars")
    List<Car> getCarsByLocationId(@PathVariable String locationId);

    @GetExchange
    List<Car> getAllCars();

    @GetExchange("/{carId}")
    Car getCarById(@PathVariable String carId);

    @GetExchange("/search/available")
    List<LocationWithAvailableCars> searchForAvailableCars(
            @RequestParam(required = false) String airportCode,
            @RequestParam(required = false) String city);
}
