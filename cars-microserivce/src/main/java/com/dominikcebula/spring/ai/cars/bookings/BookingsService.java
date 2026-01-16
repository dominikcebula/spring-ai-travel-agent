package com.dominikcebula.spring.ai.cars.bookings;

import com.dominikcebula.spring.ai.cars.cars.Car;
import com.dominikcebula.spring.ai.cars.cars.CarsService;
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
    private final CarsService carsService;

    public BookingsService(BookingsRepository bookingsRepository, CarsService carsService) {
        this.bookingsRepository = bookingsRepository;
        this.carsService = carsService;
    }

    public List<Booking> getAllBookings() {
        return bookingsRepository.findAll();
    }

    public Optional<Booking> getBookingByReference(String bookingReference) {
        return bookingsRepository.findByBookingReference(bookingReference);
    }

    public List<Booking> getBookingsByLocationId(String locationId) {
        return bookingsRepository.findByLocationId(locationId);
    }

    public Booking createBooking(CreateBookingRequest request) {
        Car car = carsService.getCarById(request.carId())
                .orElseThrow(() -> new IllegalArgumentException("Car not found: " + request.carId()));

        if (!car.locationId().equals(request.locationId())) {
            throw new IllegalArgumentException("Car does not belong to the specified location");
        }

        if (!car.available()) {
            throw new IllegalArgumentException("Car is not available: " + request.carId());
        }

        String bookingReference = generateBookingReference();
        BigDecimal totalPrice = calculateTotalPrice(car, request.pickUpDate(), request.returnDate());
        LocalDateTime now = LocalDateTime.now();

        Booking booking = new Booking(
                bookingReference,
                request.locationId(),
                request.carId(),
                request.drivers(),
                request.pickUpDate(),
                request.returnDate(),
                BookingStatus.CONFIRMED,
                totalPrice,
                now,
                now
        );

        carsService.updateCarAvailability(request.carId(), false);

        return bookingsRepository.save(booking);
    }

    public Optional<Booking> updateBooking(String bookingReference, UpdateBookingRequest request) {
        return bookingsRepository.findByBookingReference(bookingReference)
                .filter(this::isBookingModifiable)
                .map(existingBooking -> {
                    Car car = carsService.getCarById(existingBooking.carId())
                            .orElseThrow(() -> new IllegalArgumentException("Car not found: " + existingBooking.carId()));

                    BigDecimal newTotalPrice = calculateTotalPrice(car, request.pickUpDate(), request.returnDate());

                    Booking updatedBooking = existingBooking.withUpdatedDetails(
                            request.drivers(),
                            request.pickUpDate(),
                            request.returnDate(),
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
                    carsService.updateCarAvailability(booking.carId(), true);
                    return bookingsRepository.save(cancelledBooking);
                });
    }

    private boolean isBookingModifiable(Booking booking) {
        return booking.status() != BookingStatus.CANCELLED;
    }

    private BigDecimal calculateTotalPrice(Car car, java.time.LocalDate pickUpDate, java.time.LocalDate returnDate) {
        long numberOfDays = ChronoUnit.DAYS.between(pickUpDate, returnDate);
        if (numberOfDays <= 0) {
            numberOfDays = 1;
        }
        return car.pricePerDay().multiply(BigDecimal.valueOf(numberOfDays));
    }

    private String generateBookingReference() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
