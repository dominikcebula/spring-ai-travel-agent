package com.dominikcebula.spring.ai.cars.tools;

import com.dominikcebula.spring.ai.cars.api.cars.Car;
import com.dominikcebula.spring.ai.cars.api.cars.CarsApi;
import com.dominikcebula.spring.ai.cars.api.cars.Location;
import com.dominikcebula.spring.ai.cars.api.cars.LocationWithAvailableCars;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CarsTools {
    private final CarsApi carsApi;

    public CarsTools(CarsApi carsApi) {
        this.carsApi = carsApi;
    }

    @McpTool(description = "Get all locations where cars for rental are available")
    public List<Location> getAllCarRentalLocations() {
        return carsApi.getAllLocations();
    }

    @McpTool(description = "Get location of car rental service by its ID")
    public Location getCarRentalLocationById(
            @McpToolParam(description = "ID of car rental service location")
            String locationId) {
        return carsApi.getLocationById(locationId);
    }

    @McpTool(description = "Get all cars available for rental at given location")
    public List<Car> getCarsByCarRentalLocationId(
            @McpToolParam(description = "ID of car rental service location")
            String locationId) {
        return carsApi.getCarsByLocationId(locationId);
    }

    @McpTool(description = "Get all cars available for rental in all locations")
    public List<Car> getAllCarsAvailableForRent() {
        return carsApi.getAllCars();
    }

    @McpTool(description = "Get car by its ID")
    public Car getCarAvailableForRentById(@McpToolParam(description = "Car ID") String carId) {
        return carsApi.getCarById(carId);
    }

    @McpTool(description = "Search for available cars based on airport code and/or city")
    public List<LocationWithAvailableCars> searchForAvailableCarsForRent(
            @McpToolParam(required = false, description = "airport code") String airportCode,
            @McpToolParam(required = false, description = "city name") String city) {
        return carsApi.searchForAvailableCars(airportCode, city);
    }
}
