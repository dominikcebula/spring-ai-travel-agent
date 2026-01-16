package com.dominikcebula.spring.ai.cars.cars;

import com.dominikcebula.spring.ai.cars.api.cars.Car;
import com.dominikcebula.spring.ai.cars.api.cars.CarType;
import com.dominikcebula.spring.ai.cars.api.cars.Location;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
public class CarsRepository {

    private final List<Location> locations = initializeLocations();
    private final Map<String, Car> cars = new LinkedHashMap<>();

    public CarsRepository() {
        initializeCars().forEach(car -> cars.put(car.carId(), car));
    }

    public List<Location> findAllLocations() {
        return locations;
    }

    public Optional<Location> findLocationById(String locationId) {
        return locations.stream()
                .filter(location -> location.locationId().equals(locationId))
                .findFirst();
    }

    public List<Location> findLocationsByAirportCode(String airportCode) {
        return locations.stream()
                .filter(location -> location.airportCode().equals(airportCode))
                .toList();
    }

    public List<Location> findLocationsByCityName(String cityName) {
        return locations.stream()
                .filter(location -> location.cityName().equalsIgnoreCase(cityName))
                .toList();
    }

    public List<Car> findAllCars() {
        return new ArrayList<>(cars.values());
    }

    public Optional<Car> findCarById(String carId) {
        return Optional.ofNullable(cars.get(carId));
    }

    public List<Car> findCarsByLocationId(String locationId) {
        return cars.values().stream()
                .filter(car -> car.locationId().equals(locationId))
                .toList();
    }

    public List<Car> findAvailableCarsByLocationId(String locationId) {
        return cars.values().stream()
                .filter(car -> car.locationId().equals(locationId))
                .filter(Car::available)
                .toList();
    }

    public Car saveCar(Car car) {
        cars.put(car.carId(), car);
        return car;
    }

    private List<Location> initializeLocations() {
        return List.of(
                // New York (JFK)
                new Location("LOC-JFK-001", "JFK", "New York",
                        "JFK International Airport, Terminal 4, Queens, NY 11430"),
                new Location("LOC-JFK-002", "JFK", "New York",
                        "Manhattan Downtown, 123 West 42nd Street, New York, NY 10036"),

                // Los Angeles (LAX)
                new Location("LOC-LAX-001", "LAX", "Los Angeles",
                        "LAX International Airport, 1 World Way, Los Angeles, CA 90045"),
                new Location("LOC-LAX-002", "LAX", "Los Angeles",
                        "Hollywood, 6801 Hollywood Blvd, Los Angeles, CA 90028"),

                // San Francisco (SFO)
                new Location("LOC-SFO-001", "SFO", "San Francisco",
                        "SFO International Airport, San Francisco, CA 94128"),
                new Location("LOC-SFO-002", "SFO", "San Francisco",
                        "Union Square, 333 Post Street, San Francisco, CA 94108"),

                // Chicago (ORD)
                new Location("LOC-ORD-001", "ORD", "Chicago",
                        "O'Hare International Airport, 10000 W O'Hare Ave, Chicago, IL 60666"),
                new Location("LOC-ORD-002", "ORD", "Chicago",
                        "Downtown Chicago, 151 E Wacker Drive, Chicago, IL 60601"),

                // Dallas (DFW)
                new Location("LOC-DFW-001", "DFW", "Dallas",
                        "DFW International Airport, 2400 Aviation Dr, DFW Airport, TX 75261"),
                new Location("LOC-DFW-002", "DFW", "Dallas",
                        "Downtown Dallas, 1717 Main Street, Dallas, TX 75201"),

                // London (LHR)
                new Location("LOC-LHR-001", "LHR", "London",
                        "Heathrow Airport, Terminal 5, Longford TW6, UK"),
                new Location("LOC-LHR-002", "LHR", "London",
                        "Central London, 25 Piccadilly, London W1J 0AB, UK"),

                // Frankfurt (FRA)
                new Location("LOC-FRA-001", "FRA", "Frankfurt",
                        "Frankfurt Airport, Terminal 1, 60547 Frankfurt am Main, Germany"),
                new Location("LOC-FRA-002", "FRA", "Frankfurt",
                        "Frankfurt City Center, Kaiserstrasse 75, 60329 Frankfurt, Germany"),

                // Amsterdam (AMS)
                new Location("LOC-AMS-001", "AMS", "Amsterdam",
                        "Schiphol Airport, Evert van de Beekstraat 202, 1118 CP Schiphol, Netherlands"),
                new Location("LOC-AMS-002", "AMS", "Amsterdam",
                        "Amsterdam Centrum, Damrak 70, 1012 LM Amsterdam, Netherlands"),

                // Warsaw (WAW)
                new Location("LOC-WAW-001", "WAW", "Warsaw",
                        "Warsaw Chopin Airport, Zwirki i Wigury 1, 00-906 Warsaw, Poland"),
                new Location("LOC-WAW-002", "WAW", "Warsaw",
                        "Warsaw City Center, Aleje Jerozolimskie 65/79, 00-697 Warsaw, Poland"),

                // Krakow (KRK)
                new Location("LOC-KRK-001", "KRK", "Krakow",
                        "Krakow John Paul II Airport, Medweckiego 1, 32-083 Balice, Poland"),
                new Location("LOC-KRK-002", "KRK", "Krakow",
                        "Krakow Old Town, Florianska 14, 31-021 Krakow, Poland")
        );
    }

    private List<Car> initializeCars() {
        List<Car> carList = new ArrayList<>();

        // New York Locations
        carList.addAll(createCarsForLocation("LOC-JFK-001",
                new BigDecimal("45.00"), new BigDecimal("55.00"), new BigDecimal("75.00"),
                new BigDecimal("95.00"), new BigDecimal("120.00"), new BigDecimal("250.00")));
        carList.addAll(createCarsForLocation("LOC-JFK-002",
                new BigDecimal("50.00"), new BigDecimal("60.00"), new BigDecimal("80.00"),
                new BigDecimal("100.00"), new BigDecimal("130.00"), new BigDecimal("280.00")));

        // Los Angeles Locations
        carList.addAll(createCarsForLocation("LOC-LAX-001",
                new BigDecimal("40.00"), new BigDecimal("50.00"), new BigDecimal("70.00"),
                new BigDecimal("90.00"), new BigDecimal("115.00"), new BigDecimal("240.00")));
        carList.addAll(createCarsForLocation("LOC-LAX-002",
                new BigDecimal("48.00"), new BigDecimal("58.00"), new BigDecimal("78.00"),
                new BigDecimal("98.00"), new BigDecimal("125.00"), new BigDecimal("270.00")));

        // San Francisco Locations
        carList.addAll(createCarsForLocation("LOC-SFO-001",
                new BigDecimal("42.00"), new BigDecimal("52.00"), new BigDecimal("72.00"),
                new BigDecimal("92.00"), new BigDecimal("118.00"), new BigDecimal("245.00")));
        carList.addAll(createCarsForLocation("LOC-SFO-002",
                new BigDecimal("46.00"), new BigDecimal("56.00"), new BigDecimal("76.00"),
                new BigDecimal("96.00"), new BigDecimal("122.00"), new BigDecimal("260.00")));

        // Chicago Locations
        carList.addAll(createCarsForLocation("LOC-ORD-001",
                new BigDecimal("38.00"), new BigDecimal("48.00"), new BigDecimal("68.00"),
                new BigDecimal("88.00"), new BigDecimal("110.00"), new BigDecimal("230.00")));
        carList.addAll(createCarsForLocation("LOC-ORD-002",
                new BigDecimal("42.00"), new BigDecimal("52.00"), new BigDecimal("72.00"),
                new BigDecimal("92.00"), new BigDecimal("115.00"), new BigDecimal("245.00")));

        // Dallas Locations
        carList.addAll(createCarsForLocation("LOC-DFW-001",
                new BigDecimal("35.00"), new BigDecimal("45.00"), new BigDecimal("65.00"),
                new BigDecimal("85.00"), new BigDecimal("105.00"), new BigDecimal("220.00")));
        carList.addAll(createCarsForLocation("LOC-DFW-002",
                new BigDecimal("38.00"), new BigDecimal("48.00"), new BigDecimal("68.00"),
                new BigDecimal("88.00"), new BigDecimal("110.00"), new BigDecimal("235.00")));

        // London Locations
        carList.addAll(createCarsForLocation("LOC-LHR-001",
                new BigDecimal("55.00"), new BigDecimal("65.00"), new BigDecimal("85.00"),
                new BigDecimal("105.00"), new BigDecimal("135.00"), new BigDecimal("300.00")));
        carList.addAll(createCarsForLocation("LOC-LHR-002",
                new BigDecimal("60.00"), new BigDecimal("70.00"), new BigDecimal("90.00"),
                new BigDecimal("110.00"), new BigDecimal("145.00"), new BigDecimal("320.00")));

        // Frankfurt Locations
        carList.addAll(createCarsForLocation("LOC-FRA-001",
                new BigDecimal("40.00"), new BigDecimal("50.00"), new BigDecimal("70.00"),
                new BigDecimal("90.00"), new BigDecimal("115.00"), new BigDecimal("260.00")));
        carList.addAll(createCarsForLocation("LOC-FRA-002",
                new BigDecimal("45.00"), new BigDecimal("55.00"), new BigDecimal("75.00"),
                new BigDecimal("95.00"), new BigDecimal("120.00"), new BigDecimal("275.00")));

        // Amsterdam Locations
        carList.addAll(createCarsForLocation("LOC-AMS-001",
                new BigDecimal("42.00"), new BigDecimal("52.00"), new BigDecimal("72.00"),
                new BigDecimal("92.00"), new BigDecimal("118.00"), new BigDecimal("265.00")));
        carList.addAll(createCarsForLocation("LOC-AMS-002",
                new BigDecimal("48.00"), new BigDecimal("58.00"), new BigDecimal("78.00"),
                new BigDecimal("98.00"), new BigDecimal("125.00"), new BigDecimal("285.00")));

        // Warsaw Locations
        carList.addAll(createCarsForLocation("LOC-WAW-001",
                new BigDecimal("25.00"), new BigDecimal("32.00"), new BigDecimal("45.00"),
                new BigDecimal("58.00"), new BigDecimal("75.00"), new BigDecimal("150.00")));
        carList.addAll(createCarsForLocation("LOC-WAW-002",
                new BigDecimal("28.00"), new BigDecimal("35.00"), new BigDecimal("48.00"),
                new BigDecimal("62.00"), new BigDecimal("80.00"), new BigDecimal("165.00")));

        // Krakow Locations
        carList.addAll(createCarsForLocation("LOC-KRK-001",
                new BigDecimal("22.00"), new BigDecimal("28.00"), new BigDecimal("40.00"),
                new BigDecimal("52.00"), new BigDecimal("68.00"), new BigDecimal("135.00")));
        carList.addAll(createCarsForLocation("LOC-KRK-002",
                new BigDecimal("25.00"), new BigDecimal("32.00"), new BigDecimal("45.00"),
                new BigDecimal("58.00"), new BigDecimal("72.00"), new BigDecimal("145.00")));

        return carList;
    }

    private List<Car> createCarsForLocation(String locationId,
                                            BigDecimal economyPrice, BigDecimal compactPrice,
                                            BigDecimal midsizePrice, BigDecimal fullsizePrice,
                                            BigDecimal suvPrice, BigDecimal luxuryPrice) {
        String baseId = locationId.replace("LOC-", "CAR-");
        return List.of(
                new Car(baseId + "-ECO-01", locationId, CarType.ECONOMY,
                        "Toyota", "Yaris", 2024,
                        "Fuel-efficient economy car, perfect for city driving",
                        economyPrice, 5, true),
                new Car(baseId + "-ECO-02", locationId, CarType.ECONOMY,
                        "Hyundai", "Accent", 2024,
                        "Reliable economy car with modern features",
                        economyPrice, 5, true),
                new Car(baseId + "-CMP-01", locationId, CarType.COMPACT,
                        "Honda", "Civic", 2024,
                        "Popular compact car with excellent fuel economy",
                        compactPrice, 5, true),
                new Car(baseId + "-CMP-02", locationId, CarType.COMPACT,
                        "Volkswagen", "Golf", 2024,
                        "European compact car with premium feel",
                        compactPrice, 5, true),
                new Car(baseId + "-MID-01", locationId, CarType.MIDSIZE,
                        "Toyota", "Camry", 2024,
                        "Comfortable midsize sedan for longer trips",
                        midsizePrice, 5, true),
                new Car(baseId + "-MID-02", locationId, CarType.MIDSIZE,
                        "Honda", "Accord", 2024,
                        "Spacious midsize sedan with advanced safety features",
                        midsizePrice, 5, true),
                new Car(baseId + "-FUL-01", locationId, CarType.FULLSIZE,
                        "Chevrolet", "Impala", 2024,
                        "Full-size sedan with ample space for passengers and luggage",
                        fullsizePrice, 5, true),
                new Car(baseId + "-SUV-01", locationId, CarType.SUV,
                        "Toyota", "RAV4", 2024,
                        "Versatile SUV with plenty of cargo space",
                        suvPrice, 5, true),
                new Car(baseId + "-SUV-02", locationId, CarType.SUV,
                        "Ford", "Explorer", 2024,
                        "Family-friendly SUV with three-row seating",
                        suvPrice.add(new BigDecimal("20.00")), 7, true),
                new Car(baseId + "-LUX-01", locationId, CarType.LUXURY,
                        "BMW", "5 Series", 2024,
                        "Luxury sedan with premium amenities and performance",
                        luxuryPrice, 5, true),
                new Car(baseId + "-LUX-02", locationId, CarType.LUXURY,
                        "Mercedes-Benz", "E-Class", 2024,
                        "Executive luxury sedan with world-class comfort",
                        luxuryPrice.add(new BigDecimal("30.00")), 5, true),
                new Car(baseId + "-VAN-01", locationId, CarType.VAN,
                        "Chrysler", "Pacifica", 2024,
                        "Minivan with comfortable seating for the whole family",
                        suvPrice.add(new BigDecimal("15.00")), 8, true)
        );
    }
}
