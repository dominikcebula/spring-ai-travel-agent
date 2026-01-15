package com.dominikcebula.spring.ai.hotels.rooms;

import java.util.List;

public record HotelWithAvailableRooms(
        Hotel hotel,
        List<Room> availableRooms
) {
}
