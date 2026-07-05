package ru.bookingsystem.dto.response;

import ru.bookingsystem.entity.Booking;

import java.time.Instant;

public record BookingResponseDTO(
        Long id,
        Instant startTime,
        Instant endTime,
        Booking.BookingStatus bookingStatus
) {
}

