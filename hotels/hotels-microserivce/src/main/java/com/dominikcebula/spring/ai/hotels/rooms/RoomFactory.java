package com.dominikcebula.spring.ai.hotels.rooms;

import com.dominikcebula.spring.ai.hotels.api.rooms.Room;

public class RoomFactory {
    private final Room room;

    public RoomFactory(Room room) {
        this.room = room;
    }

    public Room withAvailability(boolean newAvailable) {
        return new Room(room.roomId(), room.hotelId(), room.roomType(), room.description(),
                room.pricePerNight(), room.capacity(), newAvailable);
    }
}
