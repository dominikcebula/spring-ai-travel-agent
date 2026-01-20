package com.dominikcebula.spring.ai.hotels.tools;

import com.dominikcebula.spring.ai.hotels.api.rooms.Hotel;
import com.dominikcebula.spring.ai.hotels.api.rooms.HotelWithAvailableRooms;
import com.dominikcebula.spring.ai.hotels.api.rooms.HotelsApi;
import com.dominikcebula.spring.ai.hotels.api.rooms.Room;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class HotelsTools {
    private final HotelsApi hotelsApi;

    public HotelsTools(HotelsApi hotelsApi) {
        this.hotelsApi = hotelsApi;
    }

    @McpTool(description = "Get all available hotels")
    public List<Hotel> getAllAvailableHotels() {
        return hotelsApi.getAllHotels();
    }

    @McpTool(description = "Get a hotel by its ID")
    public Hotel getHotelById(
            @McpToolParam(description = "Hotel ID")
            String hotelId) {
        return hotelsApi.getHotelById(hotelId);
    }

    @McpTool(description = "Get all rooms available at a specific hotel")
    public List<Room> getRoomsByHotelId(
            @McpToolParam(description = "Hotel ID")
            String hotelId) {
        return hotelsApi.getRoomsByHotelId(hotelId);
    }

    @McpTool(description = "Search for available hotel rooms based on airport code and/or city")
    public List<HotelWithAvailableRooms> searchForAvailableRooms(
            @McpToolParam(required = false, description = "Airport code")
            String airportCode,
            @McpToolParam(required = false, description = "City name")
            String city) {
        return hotelsApi.searchForAvailableRooms(airportCode, city);
    }
}
