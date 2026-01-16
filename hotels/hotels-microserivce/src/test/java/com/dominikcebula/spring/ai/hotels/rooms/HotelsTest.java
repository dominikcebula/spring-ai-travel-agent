package com.dominikcebula.spring.ai.hotels.rooms;

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
class HotelsTest {

    private static final String HOTELS_URL = "/api/v1/hotels";

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldListAllHotels() {
        // given
        // hotels are pre-loaded in HotelsRepository

        // when
        ResponseEntity<List<Hotel>> response = restTemplate.exchange(
                HOTELS_URL,
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
    void shouldReturnHotelsWithAllRequiredFields() {
        // given
        // hotels are pre-loaded in HotelsRepository

        // when
        ResponseEntity<List<Hotel>> response = restTemplate.exchange(
                HOTELS_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).allSatisfy(hotel -> {
            assertThat(hotel.hotelId()).isNotBlank();
            assertThat(hotel.hotelId()).startsWith("HTL-");
            assertThat(hotel.name()).isNotBlank();
            assertThat(hotel.airportCode()).hasSize(3);
            assertThat(hotel.cityName()).isNotBlank();
            assertThat(hotel.address()).isNotBlank();
            assertThat(hotel.starRating()).isBetween(1, 5);
        });
    }

    @Test
    void shouldRetrieveManhattanGrandHotelWithAllDetails() {
        // given
        String hotelId = "HTL-JFK-001";

        // when
        ResponseEntity<Hotel> response = restTemplate.getForEntity(
                HOTELS_URL + "/" + hotelId,
                Hotel.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertHotel(response.getBody(),
                "HTL-JFK-001", "The Manhattan Grand Hotel", "JFK", "New York",
                "789 Fifth Avenue, New York, NY 10022", 5);
    }

    @Test
    void shouldRetrieveTimesSquarePlazaInnWithAllDetails() {
        // given
        String hotelId = "HTL-JFK-002";

        // when
        ResponseEntity<Hotel> response = restTemplate.getForEntity(
                HOTELS_URL + "/" + hotelId,
                Hotel.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertHotel(response.getBody(),
                "HTL-JFK-002", "Times Square Plaza Inn", "JFK", "New York",
                "234 West 42nd Street, New York, NY 10036", 4);
    }

    @Test
    void shouldRetrieveWarsawRoyalCastleHotelWithAllDetails() {
        // given
        String hotelId = "HTL-WAW-001";

        // when
        ResponseEntity<Hotel> response = restTemplate.getForEntity(
                HOTELS_URL + "/" + hotelId,
                Hotel.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertHotel(response.getBody(),
                "HTL-WAW-001", "Warsaw Royal Castle Hotel", "WAW", "Warsaw",
                "Krakowskie Przedmiescie 42/44, 00-325 Warsaw", 5);
    }

    @Test
    void shouldRetrieveKrakowMainSquareHotelWithAllDetails() {
        // given
        String hotelId = "HTL-KRK-001";

        // when
        ResponseEntity<Hotel> response = restTemplate.getForEntity(
                HOTELS_URL + "/" + hotelId,
                Hotel.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertHotel(response.getBody(),
                "HTL-KRK-001", "Krakow Main Square Hotel", "KRK", "Krakow",
                "Rynek Glowny 28, 31-010 Krakow", 5);
    }

    @Test
    void shouldReturnNotFoundForNonExistentHotel() {
        // given
        String nonExistentHotelId = "HTL-XXX-999";

        // when
        ResponseEntity<Hotel> response = restTemplate.getForEntity(
                HOTELS_URL + "/" + nonExistentHotelId,
                Hotel.class
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldContainHotelsInAllExpectedCities() {
        // given
        List<String> expectedAirportCodes = List.of("JFK", "LAX", "SFO", "ORD", "DFW", "LHR", "FRA", "AMS", "WAW", "KRK");

        // when
        ResponseEntity<List<Hotel>> response = restTemplate.exchange(
                HOTELS_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody())
                .extracting(Hotel::airportCode)
                .containsAll(expectedAirportCodes);

        expectedAirportCodes.forEach(code ->
                assertThat(response.getBody())
                        .filteredOn(h -> h.airportCode().equals(code))
                        .as("Should have exactly 2 hotels for airport code %s", code)
                        .hasSize(2)
        );
    }

    @Test
    void shouldContainHotelsWithMixedStarRatings() {
        // given
        // hotels are pre-loaded in HotelsRepository

        // when
        ResponseEntity<List<Hotel>> response = restTemplate.exchange(
                HOTELS_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody())
                .extracting(Hotel::starRating)
                .contains(4, 5);

        long fiveStarCount = response.getBody().stream()
                .filter(h -> h.starRating() == 5)
                .count();
        long fourStarCount = response.getBody().stream()
                .filter(h -> h.starRating() == 4)
                .count();

        assertThat(fiveStarCount).isGreaterThan(0);
        assertThat(fourStarCount).isGreaterThan(0);
    }

    @Test
    void shouldGetRoomsByHotelIdForManhattanGrandHotel() {
        // given
        String hotelId = "HTL-JFK-001";

        // when
        ResponseEntity<List<Room>> response = restTemplate.exchange(
                HOTELS_URL + "/" + hotelId + "/rooms",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(8);
        assertThat(response.getBody()).allSatisfy(room -> {
            assertThat(room.hotelId()).isEqualTo("HTL-JFK-001");
            assertThat(room.roomId()).isNotBlank();
            assertThat(room.roomType()).isNotNull();
            assertThat(room.description()).isNotBlank();
            assertThat(room.pricePerNight()).isPositive();
            assertThat(room.capacity()).isPositive();
        });
    }

    @Test
    void shouldGetRoomsByHotelIdForWarsawRoyalCastleHotel() {
        // given
        String hotelId = "HTL-WAW-001";

        // when
        ResponseEntity<List<Room>> response = restTemplate.exchange(
                HOTELS_URL + "/" + hotelId + "/rooms",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(8);
        assertThat(response.getBody()).allSatisfy(room -> {
            assertThat(room.hotelId()).isEqualTo("HTL-WAW-001");
        });
    }

    @Test
    void shouldReturnNotFoundForRoomsOfNonExistentHotel() {
        // given
        String nonExistentHotelId = "HTL-XXX-999";

        // when
        ResponseEntity<List<Room>> response = restTemplate.exchange(
                HOTELS_URL + "/" + nonExistentHotelId + "/rooms",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldReturnRoomsWithAllRequiredFields() {
        // given
        String hotelId = "HTL-JFK-001";

        // when
        ResponseEntity<List<Room>> response = restTemplate.exchange(
                HOTELS_URL + "/" + hotelId + "/rooms",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).allSatisfy(room -> {
            assertThat(room.roomId()).isNotBlank();
            assertThat(room.hotelId()).isNotBlank();
            assertThat(room.roomType()).isNotNull();
            assertThat(room.description()).isNotBlank();
            assertThat(room.pricePerNight()).isNotNull();
            assertThat(room.pricePerNight()).isPositive();
            assertThat(room.capacity()).isPositive();
        });
    }

    @Test
    void shouldContainAllRoomTypesForHotel() {
        // given
        String hotelId = "HTL-JFK-001";

        // when
        ResponseEntity<List<Room>> response = restTemplate.exchange(
                HOTELS_URL + "/" + hotelId + "/rooms",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        assertThat(response.getBody())
                .extracting(Room::roomType)
                .contains(RoomType.SINGLE, RoomType.DOUBLE, RoomType.TWIN,
                        RoomType.DELUXE, RoomType.FAMILY, RoomType.SUITE);
    }

    @Test
    void shouldHaveRealisticPricingForDifferentRoomTypes() {
        // given
        String hotelId = "HTL-JFK-001";

        // when
        ResponseEntity<List<Room>> response = restTemplate.exchange(
                HOTELS_URL + "/" + hotelId + "/rooms",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        BigDecimal singlePrice = response.getBody().stream()
                .filter(r -> r.roomType() == RoomType.SINGLE)
                .findFirst()
                .map(Room::pricePerNight)
                .orElseThrow();

        BigDecimal suitePrice = response.getBody().stream()
                .filter(r -> r.roomType() == RoomType.SUITE)
                .findFirst()
                .map(Room::pricePerNight)
                .orElseThrow();

        assertThat(suitePrice).isGreaterThan(singlePrice);
    }

    @Test
    void shouldHaveCorrectCapacityForRoomTypes() {
        // given
        String hotelId = "HTL-JFK-001";

        // when
        ResponseEntity<List<Room>> response = restTemplate.exchange(
                HOTELS_URL + "/" + hotelId + "/rooms",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        response.getBody().forEach(room -> {
            switch (room.roomType()) {
                case SINGLE -> assertThat(room.capacity()).isEqualTo(1);
                case DOUBLE, TWIN, DELUXE, SUITE -> assertThat(room.capacity()).isEqualTo(2);
                case FAMILY -> assertThat(room.capacity()).isEqualTo(4);
            }
        });
    }

    @Test
    void shouldSearchForAvailableRoomsByAirportCodeWAW() {
        // given
        String airportCode = "WAW";

        // when
        ResponseEntity<List<HotelWithAvailableRooms>> response = restTemplate.exchange(
                HOTELS_URL + "/search/available?airportCode=" + airportCode,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).allSatisfy(hotelWithRooms -> {
            assertThat(hotelWithRooms.hotel().airportCode()).isEqualTo("WAW");
            assertThat(hotelWithRooms.hotel().cityName()).isEqualTo("Warsaw");
            assertThat(hotelWithRooms.availableRooms()).isNotEmpty();
            assertThat(hotelWithRooms.availableRooms()).allSatisfy(room -> {
                assertThat(room.available()).isTrue();
                assertThat(room.hotelId()).isEqualTo(hotelWithRooms.hotel().hotelId());
            });
        });
    }

    @Test
    void shouldSearchForAvailableRoomsByAirportCodeJFK() {
        // given
        String airportCode = "JFK";

        // when
        ResponseEntity<List<HotelWithAvailableRooms>> response = restTemplate.exchange(
                HOTELS_URL + "/search/available?airportCode=" + airportCode,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).allSatisfy(hotelWithRooms -> {
            assertThat(hotelWithRooms.hotel().airportCode()).isEqualTo("JFK");
            assertThat(hotelWithRooms.hotel().cityName()).isEqualTo("New York");
            assertThat(hotelWithRooms.availableRooms()).isNotEmpty();
        });

        assertThat(response.getBody())
                .extracting(h -> h.hotel().name())
                .contains("The Manhattan Grand Hotel", "Times Square Plaza Inn");
    }

    @Test
    void shouldSearchForAvailableRoomsByCityKrakow() {
        // given
        String city = "Krakow";

        // when
        ResponseEntity<List<HotelWithAvailableRooms>> response = restTemplate.exchange(
                HOTELS_URL + "/search/available?city=" + city,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).allSatisfy(hotelWithRooms -> {
            assertThat(hotelWithRooms.hotel().cityName()).isEqualTo("Krakow");
            assertThat(hotelWithRooms.hotel().airportCode()).isEqualTo("KRK");
            assertThat(hotelWithRooms.availableRooms()).isNotEmpty();
            assertThat(hotelWithRooms.availableRooms()).allSatisfy(room -> {
                assertThat(room.available()).isTrue();
            });
        });
    }

    @Test
    void shouldSearchForAvailableRoomsByCityLondon() {
        // given
        String city = "London";

        // when
        ResponseEntity<List<HotelWithAvailableRooms>> response = restTemplate.exchange(
                HOTELS_URL + "/search/available?city=" + city,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).allSatisfy(hotelWithRooms -> {
            assertThat(hotelWithRooms.hotel().cityName()).isEqualTo("London");
            assertThat(hotelWithRooms.hotel().airportCode()).isEqualTo("LHR");
        });

        assertThat(response.getBody())
                .extracting(h -> h.hotel().name())
                .contains("The Westminster Palace Hotel", "Kensington Gardens Inn");
    }

    @Test
    void shouldSearchForAvailableRoomsWithoutFilter() {
        // given
        // hotels are pre-loaded in HotelsRepository

        // when
        ResponseEntity<List<HotelWithAvailableRooms>> response = restTemplate.exchange(
                HOTELS_URL + "/search/available",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(20);
        assertThat(response.getBody()).allSatisfy(hotelWithRooms -> {
            assertThat(hotelWithRooms.hotel()).isNotNull();
            assertThat(hotelWithRooms.availableRooms()).isNotEmpty();
            assertThat(hotelWithRooms.availableRooms()).allSatisfy(room -> {
                assertThat(room.available()).isTrue();
                assertThat(room.hotelId()).isEqualTo(hotelWithRooms.hotel().hotelId());
            });
        });
    }

    @Test
    void shouldReturnEmptyListWhenSearchingForAvailableRoomsInNonExistentCity() {
        // given
        String nonExistentCity = "NonExistentCity";

        // when
        ResponseEntity<List<HotelWithAvailableRooms>> response = restTemplate.exchange(
                HOTELS_URL + "/search/available?city=" + nonExistentCity,
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
    void shouldReturnEmptyListWhenSearchingForAvailableRoomsWithNonExistentAirportCode() {
        // given
        String nonExistentAirportCode = "XXX";

        // when
        ResponseEntity<List<HotelWithAvailableRooms>> response = restTemplate.exchange(
                HOTELS_URL + "/search/available?airportCode=" + nonExistentAirportCode,
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
    void shouldReturnHotelWithCorrectRoomStructureInSearch() {
        // given
        String airportCode = "JFK";

        // when
        ResponseEntity<List<HotelWithAvailableRooms>> response = restTemplate.exchange(
                HOTELS_URL + "/search/available?airportCode=" + airportCode,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).hasSize(2);

        HotelWithAvailableRooms manhattanGrand = response.getBody().stream()
                .filter(h -> h.hotel().hotelId().equals("HTL-JFK-001"))
                .findFirst()
                .orElseThrow();

        assertThat(manhattanGrand.hotel().name()).isEqualTo("The Manhattan Grand Hotel");
        assertThat(manhattanGrand.hotel().cityName()).isEqualTo("New York");
        assertThat(manhattanGrand.hotel().starRating()).isEqualTo(5);
        assertThat(manhattanGrand.availableRooms()).isNotEmpty();
        assertThat(manhattanGrand.availableRooms())
                .extracting(Room::roomType)
                .contains(RoomType.SINGLE, RoomType.DOUBLE, RoomType.SUITE);
    }

    @Test
    void shouldContainHotelsFromUsAndEuropeanCities() {
        // given
        List<String> usAirports = List.of("JFK", "LAX", "SFO", "ORD", "DFW");
        List<String> euAirports = List.of("LHR", "FRA", "AMS", "WAW", "KRK");

        // when
        ResponseEntity<List<Hotel>> response = restTemplate.exchange(
                HOTELS_URL,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        List<Hotel> usHotels = response.getBody().stream()
                .filter(h -> usAirports.contains(h.airportCode()))
                .toList();

        List<Hotel> euHotels = response.getBody().stream()
                .filter(h -> euAirports.contains(h.airportCode()))
                .toList();

        assertThat(usHotels).hasSize(10);
        assertThat(euHotels).hasSize(10);
    }

    @Test
    void shouldHaveVariedPricingAcrossDifferentCities() {
        // given
        // hotels are pre-loaded in HotelsRepository

        // when
        ResponseEntity<List<HotelWithAvailableRooms>> response = restTemplate.exchange(
                HOTELS_URL + "/search/available",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();

        BigDecimal newYorkSinglePrice = response.getBody().stream()
                .filter(h -> h.hotel().hotelId().equals("HTL-JFK-001"))
                .flatMap(h -> h.availableRooms().stream())
                .filter(r -> r.roomType() == RoomType.SINGLE)
                .findFirst()
                .map(Room::pricePerNight)
                .orElseThrow();

        BigDecimal krakowSinglePrice = response.getBody().stream()
                .filter(h -> h.hotel().hotelId().equals("HTL-KRK-002"))
                .flatMap(h -> h.availableRooms().stream())
                .filter(r -> r.roomType() == RoomType.SINGLE)
                .findFirst()
                .map(Room::pricePerNight)
                .orElseThrow();

        assertThat(newYorkSinglePrice).isGreaterThan(krakowSinglePrice);
    }

    private void assertHotel(Hotel hotel,
                             String expectedHotelId, String expectedName, String expectedAirportCode,
                             String expectedCityName, String expectedAddress, int expectedStarRating) {
        assertThat(hotel.hotelId()).isEqualTo(expectedHotelId);
        assertThat(hotel.name()).isEqualTo(expectedName);
        assertThat(hotel.airportCode()).isEqualTo(expectedAirportCode);
        assertThat(hotel.cityName()).isEqualTo(expectedCityName);
        assertThat(hotel.address()).isEqualTo(expectedAddress);
        assertThat(hotel.starRating()).isEqualTo(expectedStarRating);
    }
}
