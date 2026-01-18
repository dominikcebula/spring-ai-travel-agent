package com.dominikcebula.spring.ai.hotels.api.rooms;

import java.util.List;

public record HotelWithAvailableRooms(
        Hotel hotel,
        List<Room> availableRooms
) {
}
