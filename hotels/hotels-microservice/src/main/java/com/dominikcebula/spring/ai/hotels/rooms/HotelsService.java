package com.dominikcebula.spring.ai.hotels.rooms;

import com.dominikcebula.spring.ai.hotels.api.rooms.Hotel;
import com.dominikcebula.spring.ai.hotels.api.rooms.HotelWithAvailableRooms;
import com.dominikcebula.spring.ai.hotels.api.rooms.Room;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotelsService {

    private final HotelsRepository hotelsRepository;

    public HotelsService(HotelsRepository hotelsRepository) {
        this.hotelsRepository = hotelsRepository;
    }

    public List<Hotel> getAllHotels() {
        return hotelsRepository.findAll();
    }

    public Optional<Hotel> getHotelById(String hotelId) {
        return hotelsRepository.findByHotelId(hotelId);
    }

    public List<Room> getRoomsByHotelId(String hotelId) {
        return hotelsRepository.findRoomsByHotelId(hotelId);
    }

    public Optional<Room> getRoomById(String roomId) {
        return hotelsRepository.findRoomByRoomId(roomId);
    }

    public Room updateRoomAvailability(String roomId, boolean available) {
        Room room = hotelsRepository.findRoomByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));
        Room updatedRoom = new RoomFactory(room).withAvailability(available);
        return hotelsRepository.saveRoom(updatedRoom);
    }

    public List<HotelWithAvailableRooms> searchForAvailableRooms(String airportCode, String city) {
        List<Hotel> hotels;
        if (airportCode != null) {
            hotels = hotelsRepository.findByAirportCode(airportCode);
        } else if (city != null) {
            hotels = hotelsRepository.findByCityName(city);
        } else {
            hotels = hotelsRepository.findAll();
        }

        return hotels.stream()
                .map(hotel -> new HotelWithAvailableRooms(
                        hotel,
                        hotelsRepository.findAvailableRoomsByHotelId(hotel.hotelId())
                ))
                .filter(hotelWithRooms -> !hotelWithRooms.availableRooms().isEmpty())
                .toList();
    }
}
