package com.dominikcebula.spring.ai.cars.cars;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
class CarsTest {

    private static final String CARS_URL = "/api/v1/cars";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldListAllLocations() {
        // given
        // locations are pre-loaded in CarsRepository

        // when
        ResponseEntity<List<Location>> response = restTemplate.exchange(
                CARS_URL + "/locations",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(20);
    }

    @Test
    void shouldReturnLocationsWithAllRequiredFields() {
        // given
        // locations are pre-loaded in CarsRepository

        // when
        ResponseEntity<List<Location>> response = restTemplate.exchange(
                CARS_URL + "/locations",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).allSatisfy(location -> {
            assertThat(location.locationId()).isNotBlank();
            assertThat(location.locationId()).startsWith("LOC-");
            assertThat(location.airportCode()).hasSize(3);
            assertThat(location.cityName()).isNotBlank();
            assertThat(location.address()).isNotBlank();
        });
    }

    @Test
    void shouldRetrieveJFKAirportLocationWithAllDetails() {
        // given
        String locationId = "LOC-JFK-001";

        // when
        ResponseEntity<Location> response = restTemplate.getForEntity(
                CARS_URL + "/locations/" + locationId,
                Location.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertLocation(response.getBody(),
                "LOC-JFK-001", "JFK", "New York",
                "JFK International Airport, Terminal 4, Queens, NY 11430");
    }

    @Test
    void shouldRetrieveWarsawAirportLocationWithAllDetails() {
        // given
        String locationId = "LOC-WAW-001";

        // when
        ResponseEntity<Location> response = restTemplate.getForEntity(
                CARS_URL + "/locations/" + locationId,
                Location.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertLocation(response.getBody(),
                "LOC-WAW-001", "WAW", "Warsaw",
                "Warsaw Chopin Airport, Zwirki i Wigury 1, 00-906 Warsaw, Poland");
    }

    @Test
    void shouldRetrieveKrakowCityLocationWithAllDetails() {
        // given
        String locationId = "LOC-KRK-002";

        // when
        ResponseEntity<Location> response = restTemplate.getForEntity(
                CARS_URL + "/locations/" + locationId,
                Location.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertLocation(response.getBody(),
                "LOC-KRK-002", "KRK", "Krakow",
                "Krakow Old Town, Florianska 14, 31-021 Krakow, Poland");
    }

    @Test
    void shouldReturnNotFoundForNonExistentLocation() {
        // given
        String nonExistentLocationId = "LOC-XXX-999";

        // when
        ResponseEntity<Location> response = restTemplate.getForEntity(
                CARS_URL + "/locations/" + nonExistentLocationId,
                Location.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldListAllCars() {
        // given
        // cars are pre-loaded in CarsRepository

        // when
        ResponseEntity<List<Car>> response = restTemplate.exchange(
                CARS_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(240); // 20 locations x 12 cars each
    }

    @Test
    void shouldReturnCarsWithAllRequiredFields() {
        // given
        // cars are pre-loaded in CarsRepository

        // when
        ResponseEntity<List<Car>> response = restTemplate.exchange(
                CARS_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).allSatisfy(car -> {
            assertThat(car.carId()).isNotBlank();
            assertThat(car.carId()).startsWith("CAR-");
            assertThat(car.locationId()).isNotBlank();
            assertThat(car.carType()).isNotNull();
            assertThat(car.brand()).isNotBlank();
            assertThat(car.model()).isNotBlank();
            assertThat(car.year()).isGreaterThanOrEqualTo(2020);
            assertThat(car.description()).isNotBlank();
            assertThat(car.pricePerDay()).isPositive();
            assertThat(car.seats()).isPositive();
        });
    }

    @Test
    void shouldGetCarsByLocationIdForJFKAirport() {
        // given
        String locationId = "LOC-JFK-001";

        // when
        ResponseEntity<List<Car>> response = restTemplate.exchange(
                CARS_URL + "/locations/" + locationId + "/cars",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(12);
        assertThat(response.getBody()).allSatisfy(car -> {
            assertThat(car.locationId()).isEqualTo("LOC-JFK-001");
            assertThat(car.carId()).isNotBlank();
            assertThat(car.carType()).isNotNull();
            assertThat(car.brand()).isNotBlank();
            assertThat(car.pricePerDay()).isPositive();
            assertThat(car.seats()).isPositive();
        });
    }

    @Test
    void shouldGetCarsByLocationIdForWarsawAirport() {
        // given
        String locationId = "LOC-WAW-001";

        // when
        ResponseEntity<List<Car>> response = restTemplate.exchange(
                CARS_URL + "/locations/" + locationId + "/cars",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(12);
        assertThat(response.getBody()).allSatisfy(car -> {
            assertThat(car.locationId()).isEqualTo("LOC-WAW-001");
        });
    }

    @Test
    void shouldReturnNotFoundForCarsOfNonExistentLocation() {
        // given
        String nonExistentLocationId = "LOC-XXX-999";

        // when
        ResponseEntity<List<Car>> response = restTemplate.exchange(
                CARS_URL + "/locations/" + nonExistentLocationId + "/cars",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldRetrieveCarByIdWithAllDetails() {
        // given
        String carId = "CAR-JFK-001-ECO-01";

        // when
        ResponseEntity<Car> response = restTemplate.getForEntity(
                CARS_URL + "/" + carId,
                Car.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        Car car = response.getBody();
        assertThat(car.carId()).isEqualTo("CAR-JFK-001-ECO-01");
        assertThat(car.locationId()).isEqualTo("LOC-JFK-001");
        assertThat(car.carType()).isEqualTo(CarType.ECONOMY);
        assertThat(car.brand()).isEqualTo("Toyota");
        assertThat(car.model()).isEqualTo("Yaris");
        assertThat(car.year()).isEqualTo(2024);
        assertThat(car.pricePerDay()).isEqualTo(new BigDecimal("45.00"));
        assertThat(car.seats()).isEqualTo(5);
        assertThat(car.available()).isTrue();
    }

    @Test
    void shouldReturnNotFoundForNonExistentCar() {
        // given
        String nonExistentCarId = "CAR-XXX-999-ZZZ-99";

        // when
        ResponseEntity<Car> response = restTemplate.getForEntity(
                CARS_URL + "/" + nonExistentCarId,
                Car.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldContainAllCarTypes() {
        // given
        String locationId = "LOC-JFK-001";

        // when
        ResponseEntity<List<Car>> response = restTemplate.exchange(
                CARS_URL + "/locations/" + locationId + "/cars",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody())
                .extracting(Car::carType)
                .contains(CarType.ECONOMY, CarType.COMPACT, CarType.MIDSIZE,
                        CarType.FULLSIZE, CarType.SUV, CarType.LUXURY, CarType.VAN);
    }

    @Test
    void shouldHaveRealisticPricingForDifferentCarTypes() {
        // given
        String locationId = "LOC-JFK-001";

        // when
        ResponseEntity<List<Car>> response = restTemplate.exchange(
                CARS_URL + "/locations/" + locationId + "/cars",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        BigDecimal economyPrice = response.getBody().stream()
                .filter(c -> c.carType() == CarType.ECONOMY)
                .findFirst()
                .map(Car::pricePerDay)
                .orElseThrow();

        BigDecimal luxuryPrice = response.getBody().stream()
                .filter(c -> c.carType() == CarType.LUXURY)
                .findFirst()
                .map(Car::pricePerDay)
                .orElseThrow();

        assertThat(luxuryPrice).isGreaterThan(economyPrice);
    }

    @Test
    void shouldContainLocationsInAllExpectedCities() {
        // given
        List<String> expectedAirportCodes = List.of("JFK", "LAX", "SFO", "ORD", "DFW", "LHR", "FRA", "AMS", "WAW", "KRK");

        // when
        ResponseEntity<List<Location>> response = restTemplate.exchange(
                CARS_URL + "/locations",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody())
                .extracting(Location::airportCode)
                .containsAll(expectedAirportCodes);

        expectedAirportCodes.forEach(code ->
                assertThat(response.getBody())
                        .filteredOn(l -> l.airportCode().equals(code))
                        .as("Should have exactly 2 locations for airport code %s", code)
                        .hasSize(2)
        );
    }

    @Test
    void shouldSearchForAvailableCarsByAirportCodeWAW() {
        // given
        String airportCode = "WAW";

        // when
        ResponseEntity<List<LocationWithAvailableCars>> response = restTemplate.exchange(
                CARS_URL + "/search/available?airportCode=" + airportCode,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).allSatisfy(locationWithCars -> {
            assertThat(locationWithCars.location().airportCode()).isEqualTo("WAW");
            assertThat(locationWithCars.location().cityName()).isEqualTo("Warsaw");
            assertThat(locationWithCars.availableCars()).isNotEmpty();
            assertThat(locationWithCars.availableCars()).allSatisfy(car -> {
                assertThat(car.available()).isTrue();
                assertThat(car.locationId()).isEqualTo(locationWithCars.location().locationId());
            });
        });
    }

    @Test
    void shouldSearchForAvailableCarsByAirportCodeJFK() {
        // given
        String airportCode = "JFK";

        // when
        ResponseEntity<List<LocationWithAvailableCars>> response = restTemplate.exchange(
                CARS_URL + "/search/available?airportCode=" + airportCode,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).allSatisfy(locationWithCars -> {
            assertThat(locationWithCars.location().airportCode()).isEqualTo("JFK");
            assertThat(locationWithCars.location().cityName()).isEqualTo("New York");
            assertThat(locationWithCars.availableCars()).isNotEmpty();
        });
    }

    @Test
    void shouldSearchForAvailableCarsByCityKrakow() {
        // given
        String city = "Krakow";

        // when
        ResponseEntity<List<LocationWithAvailableCars>> response = restTemplate.exchange(
                CARS_URL + "/search/available?city=" + city,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).allSatisfy(locationWithCars -> {
            assertThat(locationWithCars.location().cityName()).isEqualTo("Krakow");
            assertThat(locationWithCars.location().airportCode()).isEqualTo("KRK");
            assertThat(locationWithCars.availableCars()).isNotEmpty();
            assertThat(locationWithCars.availableCars()).allSatisfy(car -> {
                assertThat(car.available()).isTrue();
            });
        });
    }

    @Test
    void shouldSearchForAvailableCarsByCityLondon() {
        // given
        String city = "London";

        // when
        ResponseEntity<List<LocationWithAvailableCars>> response = restTemplate.exchange(
                CARS_URL + "/search/available?city=" + city,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).allSatisfy(locationWithCars -> {
            assertThat(locationWithCars.location().cityName()).isEqualTo("London");
            assertThat(locationWithCars.location().airportCode()).isEqualTo("LHR");
        });
    }

    @Test
    void shouldSearchForAvailableCarsWithoutFilter() {
        // given
        // cars are pre-loaded in CarsRepository

        // when
        ResponseEntity<List<LocationWithAvailableCars>> response = restTemplate.exchange(
                CARS_URL + "/search/available",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(20);
        assertThat(response.getBody()).allSatisfy(locationWithCars -> {
            assertThat(locationWithCars.location()).isNotNull();
            assertThat(locationWithCars.availableCars()).isNotEmpty();
            assertThat(locationWithCars.availableCars()).allSatisfy(car -> {
                assertThat(car.available()).isTrue();
                assertThat(car.locationId()).isEqualTo(locationWithCars.location().locationId());
            });
        });
    }

    @Test
    void shouldReturnEmptyListWhenSearchingForAvailableCarsInNonExistentCity() {
        // given
        String nonExistentCity = "NonExistentCity";

        // when
        ResponseEntity<List<LocationWithAvailableCars>> response = restTemplate.exchange(
                CARS_URL + "/search/available?city=" + nonExistentCity,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenSearchingForAvailableCarsWithNonExistentAirportCode() {
        // given
        String nonExistentAirportCode = "XXX";

        // when
        ResponseEntity<List<LocationWithAvailableCars>> response = restTemplate.exchange(
                CARS_URL + "/search/available?airportCode=" + nonExistentAirportCode,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void shouldReturnLocationWithCorrectCarStructureInSearch() {
        // given
        String airportCode = "JFK";

        // when
        ResponseEntity<List<LocationWithAvailableCars>> response = restTemplate.exchange(
                CARS_URL + "/search/available?airportCode=" + airportCode,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);

        LocationWithAvailableCars jfkAirport = response.getBody().stream()
                .filter(l -> l.location().locationId().equals("LOC-JFK-001"))
                .findFirst()
                .orElseThrow();

        assertThat(jfkAirport.location().cityName()).isEqualTo("New York");
        assertThat(jfkAirport.availableCars()).isNotEmpty();
        assertThat(jfkAirport.availableCars())
                .extracting(Car::carType)
                .contains(CarType.ECONOMY, CarType.COMPACT, CarType.LUXURY);
    }

    @Test
    void shouldContainLocationsFromUsAndEuropeanCities() {
        // given
        List<String> usAirports = List.of("JFK", "LAX", "SFO", "ORD", "DFW");
        List<String> euAirports = List.of("LHR", "FRA", "AMS", "WAW", "KRK");

        // when
        ResponseEntity<List<Location>> response = restTemplate.exchange(
                CARS_URL + "/locations",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        List<Location> usLocations = response.getBody().stream()
                .filter(l -> usAirports.contains(l.airportCode()))
                .toList();

        List<Location> euLocations = response.getBody().stream()
                .filter(l -> euAirports.contains(l.airportCode()))
                .toList();

        assertThat(usLocations).hasSize(10);
        assertThat(euLocations).hasSize(10);
    }

    @Test
    void shouldHaveVariedPricingAcrossDifferentCities() {
        // given
        // cars are pre-loaded in CarsRepository

        // when
        ResponseEntity<List<LocationWithAvailableCars>> response = restTemplate.exchange(
                CARS_URL + "/search/available",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        BigDecimal newYorkEconomyPrice = response.getBody().stream()
                .filter(l -> l.location().locationId().equals("LOC-JFK-001"))
                .flatMap(l -> l.availableCars().stream())
                .filter(c -> c.carType() == CarType.ECONOMY)
                .findFirst()
                .map(Car::pricePerDay)
                .orElseThrow();

        BigDecimal krakowEconomyPrice = response.getBody().stream()
                .filter(l -> l.location().locationId().equals("LOC-KRK-002"))
                .flatMap(l -> l.availableCars().stream())
                .filter(c -> c.carType() == CarType.ECONOMY)
                .findFirst()
                .map(Car::pricePerDay)
                .orElseThrow();

        assertThat(newYorkEconomyPrice).isGreaterThan(krakowEconomyPrice);
    }

    @Test
    void shouldHaveCorrectSeatsForCarTypes() {
        // given
        String locationId = "LOC-JFK-001";

        // when
        ResponseEntity<List<Car>> response = restTemplate.exchange(
                CARS_URL + "/locations/" + locationId + "/cars",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        response.getBody().forEach(car -> {
            switch (car.carType()) {
                case ECONOMY, COMPACT, MIDSIZE, FULLSIZE, LUXURY -> assertThat(car.seats()).isEqualTo(5);
                case SUV -> assertThat(car.seats()).isBetween(5, 7);
                case VAN -> assertThat(car.seats()).isGreaterThanOrEqualTo(7);
            }
        });
    }

    private void assertLocation(Location location,
                                String expectedLocationId, String expectedAirportCode,
                                String expectedCityName, String expectedAddress) {
        assertThat(location.locationId()).isEqualTo(expectedLocationId);
        assertThat(location.airportCode()).isEqualTo(expectedAirportCode);
        assertThat(location.cityName()).isEqualTo(expectedCityName);
        assertThat(location.address()).isEqualTo(expectedAddress);
    }
}
