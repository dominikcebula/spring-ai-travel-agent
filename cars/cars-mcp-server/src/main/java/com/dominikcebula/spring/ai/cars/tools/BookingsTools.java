package com.dominikcebula.spring.ai.cars.tools;

import com.dominikcebula.spring.ai.cars.api.bookings.Booking;
import com.dominikcebula.spring.ai.cars.api.bookings.BookingsApi;
import com.dominikcebula.spring.ai.cars.api.bookings.CreateBookingRequest;
import com.dominikcebula.spring.ai.cars.api.bookings.UpdateBookingRequest;
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

    @McpTool(description = "Get all car rental bookings, optionally filtered by location")
    public List<Booking> getAllBookings(
            @McpToolParam(required = false, description = "ID of car rental location to filter bookings by")
            String locationId) {
        return bookingsApi.getAllBookings(locationId);
    }

    @McpTool(description = "Get a car rental booking by its reference number")
    public Booking getBooking(
            @McpToolParam(description = "Booking reference number")
            String bookingReference) {
        return bookingsApi.getBooking(bookingReference);
    }

    @McpTool(description = "Create a new car rental booking")
    public Booking createBooking(
            @McpToolParam(description = "Booking request containing locationId, carId, drivers list (each with firstName, lastName, dateOfBirth, driverLicenseNumber, email, phoneNumber), pickUpDate, and returnDate")
            CreateBookingRequest request) {
        return bookingsApi.createBooking(request);
    }

    @McpTool(description = "Update an existing car rental booking")
    public Booking updateBooking(
            @McpToolParam(description = "Booking reference number")
            String bookingReference,
            @McpToolParam(description = "Update request containing drivers list (each with firstName, lastName, dateOfBirth, driverLicenseNumber, email, phoneNumber), pickUpDate, and returnDate")
            UpdateBookingRequest request) {
        return bookingsApi.updateBooking(bookingReference, request);
    }

    @McpTool(description = "Cancel an existing car rental booking")
    public Booking cancelBooking(
            @McpToolParam(description = "Booking reference number")
            String bookingReference) {
        return bookingsApi.cancelBooking(bookingReference);
    }
}
