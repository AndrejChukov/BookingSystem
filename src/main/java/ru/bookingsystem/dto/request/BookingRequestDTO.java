package ru.bookingsystem.dto.request;

import java.time.Instant;

public record BookingRequestDTO(Long roomId, Instant startTime, Instant endTime) {}
