package com.dominikcebula.spring.ai.hotels.rooms;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hotels")
public class HotelsController {

    private final HotelsService hotelsService;

    public HotelsController(HotelsService hotelsService) {
        this.hotelsService = hotelsService;
    }

    @GetMapping
    public List<Hotel> getAllHotels() {
        return hotelsService.getAllHotels();
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable String hotelId) {
        return hotelsService.getHotelById(hotelId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{hotelId}/rooms")
    public ResponseEntity<List<Room>> getRoomsByHotelId(@PathVariable String hotelId) {
        if (hotelsService.getHotelById(hotelId).isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(hotelsService.getRoomsByHotelId(hotelId));
    }

    @GetMapping("/search/available")
    public List<HotelWithAvailableRooms> searchForAvailableRooms(
            @RequestParam(required = false) String airportCode,
            @RequestParam(required = false) String city) {

        return hotelsService.searchForAvailableRooms(airportCode, city);
    }
}
