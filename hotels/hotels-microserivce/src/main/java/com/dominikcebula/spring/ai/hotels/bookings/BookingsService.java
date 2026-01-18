package com.dominikcebula.spring.ai.hotels.bookings;

import com.dominikcebula.spring.ai.hotels.api.bookings.Booking;
import com.dominikcebula.spring.ai.hotels.api.bookings.BookingStatus;
import com.dominikcebula.spring.ai.hotels.api.bookings.CreateBookingRequest;
import com.dominikcebula.spring.ai.hotels.api.bookings.UpdateBookingRequest;
import com.dominikcebula.spring.ai.hotels.api.rooms.Room;
import com.dominikcebula.spring.ai.hotels.rooms.HotelsService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookingsService {

    private final BookingsRepository bookingsRepository;
    private final HotelsService hotelsService;

    public BookingsService(BookingsRepository bookingsRepository, HotelsService hotelsService) {
        this.bookingsRepository = bookingsRepository;
        this.hotelsService = hotelsService;
    }

    public List<Booking> getAllBookings() {
        return bookingsRepository.findAll();
    }

    public Optional<Booking> getBookingByReference(String bookingReference) {
        return bookingsRepository.findByBookingReference(bookingReference);
    }

    public List<Booking> getBookingsByHotelId(String hotelId) {
        return bookingsRepository.findByHotelId(hotelId);
    }

    public Booking createBooking(CreateBookingRequest request) {
        Room room = hotelsService.getRoomById(request.roomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + request.roomId()));

        if (!room.hotelId().equals(request.hotelId())) {
            throw new IllegalArgumentException("Room does not belong to the specified hotel");
        }

        if (!room.available()) {
            throw new IllegalArgumentException("Room is not available: " + request.roomId());
        }

        String bookingReference = generateBookingReference();
        BigDecimal totalPrice = calculateTotalPrice(room, request.checkInDate(), request.checkOutDate());
        LocalDateTime now = LocalDateTime.now();

        Booking booking = new Booking(
                bookingReference,
                request.hotelId(),
                request.roomId(),
                request.guests(),
                request.checkInDate(),
                request.checkOutDate(),
                BookingStatus.CONFIRMED,
                totalPrice,
                now,
                now
        );

        hotelsService.updateRoomAvailability(request.roomId(), false);

        return bookingsRepository.save(booking);
    }

    public Optional<Booking> updateBooking(String bookingReference, UpdateBookingRequest request) {
        return bookingsRepository.findByBookingReference(bookingReference)
                .filter(this::isBookingModifiable)
                .map(existingBooking -> {
                    Room room = hotelsService.getRoomById(existingBooking.roomId())
                            .orElseThrow(() -> new IllegalArgumentException("Room not found: " + existingBooking.roomId()));

                    BigDecimal newTotalPrice = calculateTotalPrice(room, request.checkInDate(), request.checkOutDate());

                    Booking updatedBooking = new BookingFactory(existingBooking).withUpdatedDetails(
                            request.guests(),
                            request.checkInDate(),
                            request.checkOutDate(),
                            newTotalPrice
                    );

                    return bookingsRepository.save(updatedBooking);
                });
    }

    public Optional<Booking> cancelBooking(String bookingReference) {
        return bookingsRepository.findByBookingReference(bookingReference)
                .filter(this::isBookingModifiable)
                .map(booking -> {
                    Booking cancelledBooking = new BookingFactory(booking).withStatus(BookingStatus.CANCELLED);
                    hotelsService.updateRoomAvailability(booking.roomId(), true);
                    return bookingsRepository.save(cancelledBooking);
                });
    }

    private boolean isBookingModifiable(Booking booking) {
        return booking.status() != BookingStatus.CANCELLED;
    }

    private BigDecimal calculateTotalPrice(Room room, java.time.LocalDate checkInDate, java.time.LocalDate checkOutDate) {
        long numberOfNights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        if (numberOfNights <= 0) {
            numberOfNights = 1;
        }
        return room.pricePerNight().multiply(BigDecimal.valueOf(numberOfNights));
    }

    private String generateBookingReference() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
