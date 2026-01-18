package com.dominikcebula.spring.ai.hotels.api.rooms;

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
}
