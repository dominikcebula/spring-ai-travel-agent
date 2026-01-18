package com.dominikcebula.spring.ai.hotels.api.rooms;

public record Hotel(
        String hotelId,
        String name,
        String airportCode,
        String cityName,
        String address,
        int starRating
) {
}
