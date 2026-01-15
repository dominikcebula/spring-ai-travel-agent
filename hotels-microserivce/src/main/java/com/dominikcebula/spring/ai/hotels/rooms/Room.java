package com.dominikcebula.spring.ai.hotels.rooms;

import java.math.BigDecimal;

public record Room(
        String roomId,
        String hotelId,
        RoomType roomType,
        String description,
        BigDecimal pricePerNight,
        int capacity,
        boolean available
) {
    public Room withAvailability(boolean newAvailable) {
        return new Room(roomId, hotelId, roomType, description, pricePerNight, capacity, newAvailable);
    }
}
