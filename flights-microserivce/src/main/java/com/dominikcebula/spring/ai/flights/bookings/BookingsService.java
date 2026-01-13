package com.dominikcebula.spring.ai.flights.bookings;

import com.dominikcebula.spring.ai.flights.flights.Flight;
import com.dominikcebula.spring.ai.flights.flights.FlightService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingsService {

    private final BookingsRepository bookingsRepository;
    private final FlightService flightService;

    public BookingsService(BookingsRepository bookingsRepository, FlightService flightService) {
        this.bookingsRepository = bookingsRepository;
        this.flightService = flightService;
    }

    public List<Booking> getAllBookings() {
        return bookingsRepository.findAll();
    }

    public Optional<Booking> getBookingByReference(String bookingReference) {
        return bookingsRepository.findByBookingReference(bookingReference);
    }

    public Booking createBooking(CreateBookingRequest request) {
        validateFlightsExist(request.flightNumbers());

        String bookingReference = generateBookingReference();
        BigDecimal totalPrice = calculateTotalPrice(request.flightNumbers(), request.passengers().size());
        LocalDateTime now = LocalDateTime.now();

        Booking booking = new Booking(
                bookingReference,
                request.passengers(),
                request.flightNumbers(),
                request.travelDate(),
                BookingStatus.CONFIRMED,
                totalPrice,
                now,
                now
        );

        return bookingsRepository.save(booking);
    }

    public Optional<Booking> updateBooking(String bookingReference, UpdateBookingRequest request) {
        return bookingsRepository.findByBookingReference(bookingReference)
                .filter(this::isBookingModifiable)
                .map(existingBooking -> {
                    validateFlightsExist(request.flightNumbers());
                    BigDecimal newTotalPrice = calculateTotalPrice(request.flightNumbers(), request.passengers().size());

                    Booking updatedBooking = existingBooking.withUpdatedDetails(
                            request.passengers(),
                            request.flightNumbers(),
                            request.travelDate(),
                            newTotalPrice
                    );

                    return bookingsRepository.save(updatedBooking);
                });
    }

    public Optional<Booking> cancelBooking(String bookingReference) {
        return bookingsRepository.findByBookingReference(bookingReference)
                .filter(this::isBookingModifiable)
                .map(booking -> {
                    Booking cancelledBooking = booking.withStatus(BookingStatus.CANCELLED);
                    return bookingsRepository.save(cancelledBooking);
                });
    }

    private boolean isBookingModifiable(Booking booking) {
        return booking.status() != BookingStatus.CANCELLED;
    }

    private void validateFlightsExist(List<String> flightNumbers) {
        for (String flightNumber : flightNumbers) {
            flightService.getFlightByNumber(flightNumber)
                    .orElseThrow(() -> new IllegalArgumentException("Flight not found: " + flightNumber));
        }
    }

    private BigDecimal calculateTotalPrice(List<String> flightNumbers, int passengerCount) {
        BigDecimal flightsTotal = flightNumbers.stream()
                .map(flightService::getFlightByNumber)
                .flatMap(Optional::stream)
                .map(Flight::priceUsd)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return flightsTotal.multiply(BigDecimal.valueOf(passengerCount));
    }

    private String generateBookingReference() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
