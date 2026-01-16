package com.dominikcebula.spring.ai.cars.cars;

import com.dominikcebula.spring.ai.cars.api.cars.Car;
import com.dominikcebula.spring.ai.cars.api.cars.Location;
import com.dominikcebula.spring.ai.cars.api.cars.LocationWithAvailableCars;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarsService {

    private final CarsRepository carsRepository;

    public CarsService(CarsRepository carsRepository) {
        this.carsRepository = carsRepository;
    }

    public List<Location> getAllLocations() {
        return carsRepository.findAllLocations();
    }

    public Optional<Location> getLocationById(String locationId) {
        return carsRepository.findLocationById(locationId);
    }

    public List<Car> getAllCars() {
        return carsRepository.findAllCars();
    }

    public List<Car> getCarsByLocationId(String locationId) {
        return carsRepository.findCarsByLocationId(locationId);
    }

    public Optional<Car> getCarById(String carId) {
        return carsRepository.findCarById(carId);
    }

    public Car updateCarAvailability(String carId, boolean available) {
        Car car = carsRepository.findCarById(carId)
                .orElseThrow(() -> new IllegalArgumentException("Car not found: " + carId));
        Car updatedCar = new CarFactory(car).withAvailability(available);
        return carsRepository.saveCar(updatedCar);
    }

    public List<LocationWithAvailableCars> searchForAvailableCars(String airportCode, String city) {
        List<Location> locations;
        if (airportCode != null) {
            locations = carsRepository.findLocationsByAirportCode(airportCode);
        } else if (city != null) {
            locations = carsRepository.findLocationsByCityName(city);
        } else {
            locations = carsRepository.findAllLocations();
        }

        return locations.stream()
                .map(location -> new LocationWithAvailableCars(
                        location,
                        carsRepository.findAvailableCarsByLocationId(location.locationId())
                ))
                .filter(locationWithCars -> !locationWithCars.availableCars().isEmpty())
                .toList();
    }
}
