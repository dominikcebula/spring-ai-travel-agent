package com.dominikcebula.spring.ai.flights.tools;

import com.dominikcebula.spring.ai.flights.api.flights.Flight;
import com.dominikcebula.spring.ai.flights.api.flights.FlightsApi;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlightsTools {
    private final FlightsApi flightsApi;

    public FlightsTools(FlightsApi flightsApi) {
        this.flightsApi = flightsApi;
    }

    @McpTool(description = "Get all available flights, optionally filtered by departure and/or arrival airport")
    public List<Flight> getAllAvailableFlights(
            @McpToolParam(required = false, description = "Departure airport code")
            String departure,
            @McpToolParam(required = false, description = "Arrival airport code")
            String arrival) {
        return flightsApi.getAllFlights(departure, arrival);
    }

    @McpTool(description = "Get a flight by its flight number")
    public Flight getFlightByNumber(
            @McpToolParam(description = "Flight number")
            String flightNumber) {
        return flightsApi.getFlightByNumber(flightNumber);
    }
}
