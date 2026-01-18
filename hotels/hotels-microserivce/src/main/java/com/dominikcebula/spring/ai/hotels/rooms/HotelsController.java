package com.dominikcebula.spring.ai.hotels.rooms;

import com.dominikcebula.spring.ai.hotels.api.rooms.Hotel;
import com.dominikcebula.spring.ai.hotels.api.rooms.HotelWithAvailableRooms;
import com.dominikcebula.spring.ai.hotels.api.rooms.HotelsApi;
import com.dominikcebula.spring.ai.hotels.api.rooms.Room;
import com.dominikcebula.spring.ai.hotels.exception.ResourceNotFoundException;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class HotelsController implements HotelsApi {

    private final HotelsService hotelsService;

    public HotelsController(HotelsService hotelsService) {
        this.hotelsService = hotelsService;
    }

    @Override
    public List<Hotel> getAllHotels() {
        return hotelsService.getAllHotels();
    }

    @Override
    public Hotel getHotelById(String hotelId) {
        return hotelsService.getHotelById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found: " + hotelId));
    }

    @Override
    public List<Room> getRoomsByHotelId(String hotelId) {
        if (hotelsService.getHotelById(hotelId).isEmpty()) {
            throw new ResourceNotFoundException("Hotel not found: " + hotelId);
        }

        return hotelsService.getRoomsByHotelId(hotelId);
    }

    @Override
    public List<HotelWithAvailableRooms> searchForAvailableRooms(String airportCode, String city) {
        return hotelsService.searchForAvailableRooms(airportCode, city);
    }
}
