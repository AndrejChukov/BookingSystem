package ru.bookingsystem.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public record BookingRequestDTO(
        @NotNull
        Long roomId,
        @NotNull
        Instant startTime,
        @NotNull
        Instant endTime) {}
