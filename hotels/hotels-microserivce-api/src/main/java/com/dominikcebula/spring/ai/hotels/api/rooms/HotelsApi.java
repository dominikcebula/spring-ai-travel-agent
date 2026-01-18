package com.dominikcebula.spring.ai.hotels.api.rooms;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange("/api/v1/hotels")
public interface HotelsApi {

    @GetExchange
    List<Hotel> getAllHotels();

    @GetExchange("/{hotelId}")
    Hotel getHotelById(@PathVariable String hotelId);

    @GetExchange("/{hotelId}/rooms")
    List<Room> getRoomsByHotelId(@PathVariable String hotelId);

    @GetExchange("/search/available")
    List<HotelWithAvailableRooms> searchForAvailableRooms(
            @RequestParam(required = false) String airportCode,
            @RequestParam(required = false) String city);
}
