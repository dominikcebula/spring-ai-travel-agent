package com.dominikcebula.spring.ai.cars.cars;

import com.dominikcebula.spring.ai.cars.api.cars.Car;
import com.dominikcebula.spring.ai.cars.api.cars.CarsApi;
import com.dominikcebula.spring.ai.cars.api.cars.Location;
import com.dominikcebula.spring.ai.cars.api.cars.LocationWithAvailableCars;
import com.dominikcebula.spring.ai.cars.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CarsController implements CarsApi {

    private final CarsService carsService;

    public CarsController(CarsService carsService) {
        this.carsService = carsService;
    }

    @Override
    public List<Location> getAllLocations() {
        return carsService.getAllLocations();
    }

    @Override
    public Location getLocationById(String locationId) {
        return carsService.getLocationById(locationId)
                .orElseThrow(() -> new ResourceNotFoundException("Location not found: " + locationId));
    }

    @Override
    public List<Car> getCarsByLocationId(String locationId) {
        if (carsService.getLocationById(locationId).isEmpty()) {
            throw new ResourceNotFoundException("Location not found: " + locationId);
        }
        return carsService.getCarsByLocationId(locationId);
    }

    @Override
    public List<Car> getAllCars() {
        return carsService.getAllCars();
    }

    @Override
    public Car getCarById(String carId) {
        return carsService.getCarById(carId)
                .orElseThrow(() -> new ResourceNotFoundException("Car not found: " + carId));
    }

    @Override
    public List<LocationWithAvailableCars> searchForAvailableCars(String airportCode, String city) {
        return carsService.searchForAvailableCars(airportCode, city);
    }
}
