package com.dominikcebula.spring.ai.flights.tools;

import com.dominikcebula.spring.ai.flights.api.bookings.Booking;
import com.dominikcebula.spring.ai.flights.api.bookings.BookingsApi;
import com.dominikcebula.spring.ai.flights.api.bookings.CreateBookingRequest;
import com.dominikcebula.spring.ai.flights.api.bookings.UpdateBookingRequest;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingsTools {
    private final BookingsApi bookingsApi;

    public BookingsTools(BookingsApi bookingsApi) {
        this.bookingsApi = bookingsApi;
    }

    @McpTool(description = "Get all flight bookings")
    public List<Booking> getAllFlightsBookings() {
        return bookingsApi.getAllBookings();
    }

    @McpTool(description = "Get a flight booking by its reference number")
    public Booking getFlightBooking(
            @McpToolParam(description = "Booking reference number")
            String bookingReference) {
        return bookingsApi.getBooking(bookingReference);
    }

    @McpTool(description = "Create a new flight booking")
    public Booking createFlightBooking(
            @McpToolParam(description = "Booking request containing passengers list (each with firstName, lastName, dateOfBirth, passportNumber, email, phoneNumber), flightNumbers list, and travelDate")
            CreateBookingRequest request) {
        return bookingsApi.createBooking(request);
    }

    @McpTool(description = "Update an existing flight booking")
    public Booking updateFlightBooking(
            @McpToolParam(description = "Booking reference number")
            String bookingReference,
            @McpToolParam(description = "Update request containing passengers list (each with firstName, lastName, dateOfBirth, passportNumber, email, phoneNumber), flightNumbers list, and travelDate")
            UpdateBookingRequest request) {
        return bookingsApi.updateBooking(bookingReference, request);
    }

    @McpTool(description = "Cancel an existing flight booking")
    public Booking cancelFlightBooking(
            @McpToolParam(description = "Booking reference number")
            String bookingReference) {
        return bookingsApi.cancelBooking(bookingReference);
    }
}
