package com.dominikcebula.spring.ai.hotels.rooms;

import com.dominikcebula.spring.ai.hotels.api.rooms.Hotel;
import com.dominikcebula.spring.ai.hotels.api.rooms.Room;
import com.dominikcebula.spring.ai.hotels.api.rooms.RoomType;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
public class HotelsRepository {

    private final List<Hotel> hotels = initializeHotels();
    private final Map<String, Room> rooms = new LinkedHashMap<>();

    public HotelsRepository() {
        initializeRooms().forEach(room -> rooms.put(room.roomId(), room));
    }

    public List<Hotel> findAll() {
        return hotels;
    }

    public Optional<Hotel> findByHotelId(String hotelId) {
        return hotels.stream()
                .filter(hotel -> hotel.hotelId().equals(hotelId))
                .findFirst();
    }

    public List<Hotel> findByAirportCode(String airportCode) {
        return hotels.stream()
                .filter(hotel -> hotel.airportCode().equals(airportCode))
                .toList();
    }

    public List<Hotel> findByCityName(String cityName) {
        return hotels.stream()
                .filter(hotel -> hotel.cityName().equalsIgnoreCase(cityName))
                .toList();
    }

    public Optional<Room> findRoomByRoomId(String roomId) {
        return Optional.ofNullable(rooms.get(roomId));
    }

    public List<Room> findRoomsByHotelId(String hotelId) {
        return rooms.values().stream()
                .filter(room -> room.hotelId().equals(hotelId))
                .toList();
    }

    public List<Room> findAvailableRoomsByHotelId(String hotelId) {
        return rooms.values().stream()
                .filter(room -> room.hotelId().equals(hotelId))
                .filter(Room::available)
                .toList();
    }

    public Room saveRoom(Room room) {
        rooms.put(room.roomId(), room);
        return room;
    }

    private List<Hotel> initializeHotels() {
        return List.of(
                // New York (JFK)
                new Hotel("HTL-JFK-001", "The Manhattan Grand Hotel", "JFK", "New York",
                        "789 Fifth Avenue, New York, NY 10022", 5),
                new Hotel("HTL-JFK-002", "Times Square Plaza Inn", "JFK", "New York",
                        "234 West 42nd Street, New York, NY 10036", 4),

                // Los Angeles (LAX)
                new Hotel("HTL-LAX-001", "Beverly Hills Luxury Resort", "LAX", "Los Angeles",
                        "9876 Wilshire Boulevard, Beverly Hills, CA 90210", 5),
                new Hotel("HTL-LAX-002", "Santa Monica Beachfront Hotel", "LAX", "Los Angeles",
                        "1500 Ocean Avenue, Santa Monica, CA 90401", 4),

                // San Francisco (SFO)
                new Hotel("HTL-SFO-001", "Golden Gate View Hotel", "SFO", "San Francisco",
                        "500 Post Street, San Francisco, CA 94102", 4),
                new Hotel("HTL-SFO-002", "Fisherman's Wharf Suites", "SFO", "San Francisco",
                        "2620 Jones Street, San Francisco, CA 94133", 4),

                // Chicago (ORD)
                new Hotel("HTL-ORD-001", "The Chicago Lakeshore Hotel", "ORD", "Chicago",
                        "180 East Pearson Street, Chicago, IL 60611", 5),
                new Hotel("HTL-ORD-002", "Magnificent Mile Inn", "ORD", "Chicago",
                        "625 North Michigan Avenue, Chicago, IL 60611", 4),

                // Dallas (DFW)
                new Hotel("HTL-DFW-001", "Dallas Downtown Grand", "DFW", "Dallas",
                        "1321 Commerce Street, Dallas, TX 75202", 4),
                new Hotel("HTL-DFW-002", "The Texan Luxury Hotel", "DFW", "Dallas",
                        "2100 McKinney Avenue, Dallas, TX 75201", 5),

                // London (LHR)
                new Hotel("HTL-LHR-001", "The Westminster Palace Hotel", "LHR", "London",
                        "15 Victoria Street, London SW1H 0NE", 5),
                new Hotel("HTL-LHR-002", "Kensington Gardens Inn", "LHR", "London",
                        "48 Bayswater Road, London W2 3JH", 4),

                // Frankfurt (FRA)
                new Hotel("HTL-FRA-001", "Frankfurt Hauptbahnhof Grand", "FRA", "Frankfurt",
                        "Kaiserstrasse 50, 60329 Frankfurt am Main", 4),
                new Hotel("HTL-FRA-002", "Sachsenhausen Riverside Hotel", "FRA", "Frankfurt",
                        "Schweizer Strasse 12, 60594 Frankfurt am Main", 4),

                // Amsterdam (AMS)
                new Hotel("HTL-AMS-001", "The Canal House Hotel", "AMS", "Amsterdam",
                        "Herengracht 341, 1016 AZ Amsterdam", 5),
                new Hotel("HTL-AMS-002", "Vondelpark Garden Hotel", "AMS", "Amsterdam",
                        "Stadhouderskade 25, 1071 ZD Amsterdam", 4),

                // Warsaw (WAW)
                new Hotel("HTL-WAW-001", "Warsaw Royal Castle Hotel", "WAW", "Warsaw",
                        "Krakowskie Przedmiescie 42/44, 00-325 Warsaw", 5),
                new Hotel("HTL-WAW-002", "Old Town Residence Warsaw", "WAW", "Warsaw",
                        "Swietojanska 15, 00-266 Warsaw", 4),

                // Krakow (KRK)
                new Hotel("HTL-KRK-001", "Krakow Main Square Hotel", "KRK", "Krakow",
                        "Rynek Glowny 28, 31-010 Krakow", 5),
                new Hotel("HTL-KRK-002", "Kazimierz Historic Inn", "KRK", "Krakow",
                        "Szeroka 18, 31-053 Krakow", 4)
        );
    }

    private List<Room> initializeRooms() {
        List<Room> roomList = new ArrayList<>();

        // New York Hotels
        roomList.addAll(createRoomsForHotel("HTL-JFK-001",
                new BigDecimal("450.00"), new BigDecimal("550.00"), new BigDecimal("750.00"), new BigDecimal("1200.00")));
        roomList.addAll(createRoomsForHotel("HTL-JFK-002",
                new BigDecimal("280.00"), new BigDecimal("350.00"), new BigDecimal("480.00"), new BigDecimal("750.00")));

        // Los Angeles Hotels
        roomList.addAll(createRoomsForHotel("HTL-LAX-001",
                new BigDecimal("520.00"), new BigDecimal("620.00"), new BigDecimal("850.00"), new BigDecimal("1500.00")));
        roomList.addAll(createRoomsForHotel("HTL-LAX-002",
                new BigDecimal("320.00"), new BigDecimal("420.00"), new BigDecimal("580.00"), new BigDecimal("900.00")));

        // San Francisco Hotels
        roomList.addAll(createRoomsForHotel("HTL-SFO-001",
                new BigDecimal("380.00"), new BigDecimal("480.00"), new BigDecimal("650.00"), new BigDecimal("1100.00")));
        roomList.addAll(createRoomsForHotel("HTL-SFO-002",
                new BigDecimal("290.00"), new BigDecimal("380.00"), new BigDecimal("520.00"), new BigDecimal("850.00")));

        // Chicago Hotels
        roomList.addAll(createRoomsForHotel("HTL-ORD-001",
                new BigDecimal("420.00"), new BigDecimal("520.00"), new BigDecimal("720.00"), new BigDecimal("1150.00")));
        roomList.addAll(createRoomsForHotel("HTL-ORD-002",
                new BigDecimal("260.00"), new BigDecimal("340.00"), new BigDecimal("460.00"), new BigDecimal("720.00")));

        // Dallas Hotels
        roomList.addAll(createRoomsForHotel("HTL-DFW-001",
                new BigDecimal("240.00"), new BigDecimal("320.00"), new BigDecimal("440.00"), new BigDecimal("680.00")));
        roomList.addAll(createRoomsForHotel("HTL-DFW-002",
                new BigDecimal("380.00"), new BigDecimal("480.00"), new BigDecimal("650.00"), new BigDecimal("1050.00")));

        // London Hotels
        roomList.addAll(createRoomsForHotel("HTL-LHR-001",
                new BigDecimal("480.00"), new BigDecimal("580.00"), new BigDecimal("780.00"), new BigDecimal("1350.00")));
        roomList.addAll(createRoomsForHotel("HTL-LHR-002",
                new BigDecimal("320.00"), new BigDecimal("420.00"), new BigDecimal("560.00"), new BigDecimal("880.00")));

        // Frankfurt Hotels
        roomList.addAll(createRoomsForHotel("HTL-FRA-001",
                new BigDecimal("220.00"), new BigDecimal("300.00"), new BigDecimal("420.00"), new BigDecimal("650.00")));
        roomList.addAll(createRoomsForHotel("HTL-FRA-002",
                new BigDecimal("180.00"), new BigDecimal("260.00"), new BigDecimal("360.00"), new BigDecimal("550.00")));

        // Amsterdam Hotels
        roomList.addAll(createRoomsForHotel("HTL-AMS-001",
                new BigDecimal("350.00"), new BigDecimal("450.00"), new BigDecimal("620.00"), new BigDecimal("980.00")));
        roomList.addAll(createRoomsForHotel("HTL-AMS-002",
                new BigDecimal("240.00"), new BigDecimal("320.00"), new BigDecimal("450.00"), new BigDecimal("700.00")));

        // Warsaw Hotels
        roomList.addAll(createRoomsForHotel("HTL-WAW-001",
                new BigDecimal("180.00"), new BigDecimal("250.00"), new BigDecimal("350.00"), new BigDecimal("580.00")));
        roomList.addAll(createRoomsForHotel("HTL-WAW-002",
                new BigDecimal("120.00"), new BigDecimal("180.00"), new BigDecimal("260.00"), new BigDecimal("420.00")));

        // Krakow Hotels
        roomList.addAll(createRoomsForHotel("HTL-KRK-001",
                new BigDecimal("160.00"), new BigDecimal("220.00"), new BigDecimal("320.00"), new BigDecimal("520.00")));
        roomList.addAll(createRoomsForHotel("HTL-KRK-002",
                new BigDecimal("100.00"), new BigDecimal("150.00"), new BigDecimal("220.00"), new BigDecimal("380.00")));

        return roomList;
    }

    private List<Room> createRoomsForHotel(String hotelId,
                                           BigDecimal singlePrice, BigDecimal doublePrice,
                                           BigDecimal deluxePrice, BigDecimal suitePrice) {
        String baseId = hotelId.replace("HTL-", "");
        return List.of(
                new Room(baseId + "-SGL-101", hotelId, RoomType.SINGLE,
                        "Comfortable single room with city view", singlePrice, 1, true),
                new Room(baseId + "-SGL-102", hotelId, RoomType.SINGLE,
                        "Cozy single room with modern amenities", singlePrice, 1, true),
                new Room(baseId + "-DBL-201", hotelId, RoomType.DOUBLE,
                        "Spacious double room with king-size bed", doublePrice, 2, true),
                new Room(baseId + "-DBL-202", hotelId, RoomType.DOUBLE,
                        "Elegant double room with garden view", doublePrice, 2, true),
                new Room(baseId + "-TWN-301", hotelId, RoomType.TWIN,
                        "Twin room with two comfortable beds", doublePrice, 2, true),
                new Room(baseId + "-DLX-401", hotelId, RoomType.DELUXE,
                        "Deluxe room with premium furnishings and minibar", deluxePrice, 2, true),
                new Room(baseId + "-FAM-501", hotelId, RoomType.FAMILY,
                        "Family room with space for up to 4 guests", deluxePrice.add(new BigDecimal("100.00")), 4, true),
                new Room(baseId + "-STE-601", hotelId, RoomType.SUITE,
                        "Luxury suite with separate living area and premium amenities", suitePrice, 2, true)
        );
    }
}
