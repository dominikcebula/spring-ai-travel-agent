package com.dominikcebula.spring.ai.cars.cars;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
public class CarsController {

    private final CarsService carsService;

    public CarsController(CarsService carsService) {
        this.carsService = carsService;
    }

    @GetMapping("/locations")
    public List<Location> getAllLocations() {
        return carsService.getAllLocations();
    }

    @GetMapping("/locations/{locationId}")
    public ResponseEntity<Location> getLocationById(@PathVariable String locationId) {
        return carsService.getLocationById(locationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/locations/{locationId}/cars")
    public ResponseEntity<List<Car>> getCarsByLocationId(@PathVariable String locationId) {
        if (carsService.getLocationById(locationId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(carsService.getCarsByLocationId(locationId));
    }

    @GetMapping
    public List<Car> getAllCars() {
        return carsService.getAllCars();
    }

    @GetMapping("/{carId}")
    public ResponseEntity<Car> getCarById(@PathVariable String carId) {
        return carsService.getCarById(carId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/available")
    public List<LocationWithAvailableCars> searchForAvailableCars(
            @RequestParam(required = false) String airportCode,
            @RequestParam(required = false) String city) {

        return carsService.searchForAvailableCars(airportCode, city);
    }
}
