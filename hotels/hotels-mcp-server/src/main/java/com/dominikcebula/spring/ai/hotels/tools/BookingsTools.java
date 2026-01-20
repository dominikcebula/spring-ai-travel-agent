package com.dominikcebula.spring.ai.hotels.tools;

import com.dominikcebula.spring.ai.hotels.api.bookings.Booking;
import com.dominikcebula.spring.ai.hotels.api.bookings.BookingsApi;
import com.dominikcebula.spring.ai.hotels.api.bookings.CreateBookingRequest;
import com.dominikcebula.spring.ai.hotels.api.bookings.UpdateBookingRequest;
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

    @McpTool(description = "Get all hotel bookings, optionally filtered by hotel")
    public List<Booking> getAllHotelsBookingsByHotelId(
            @McpToolParam(required = false, description = "Hotel ID to filter bookings by")
            String hotelId) {
        return bookingsApi.getAllBookings(hotelId);
    }

    @McpTool(description = "Get a hotel booking by its reference number")
    public Booking getHotelBookingsByBookingReference(
            @McpToolParam(description = "Booking reference number")
            String bookingReference) {
        return bookingsApi.getBooking(bookingReference);
    }

    @McpTool(description = "Create a new hotel booking")
    public Booking createHotelBooking(
            @McpToolParam(description = "Booking request containing hotelId, roomId, guests list (each with firstName, lastName, dateOfBirth, passportNumber, email, phoneNumber), checkInDate, and checkOutDate")
            CreateBookingRequest request) {
        return bookingsApi.createBooking(request);
    }

    @McpTool(description = "Update an existing hotel booking")
    public Booking updateHotelBooking(
            @McpToolParam(description = "Booking reference number")
            String bookingReference,
            @McpToolParam(description = "Update request containing guests list (each with firstName, lastName, dateOfBirth, passportNumber, email, phoneNumber), checkInDate, and checkOutDate")
            UpdateBookingRequest request) {
        return bookingsApi.updateBooking(bookingReference, request);
    }

    @McpTool(description = "Cancel an existing hotel booking")
    public Booking cancelHotelBooking(
            @McpToolParam(description = "Booking reference number")
            String bookingReference) {
        return bookingsApi.cancelBooking(bookingReference);
    }
}
