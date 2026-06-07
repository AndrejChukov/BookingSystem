package ru.bookingsystem.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.bookingsystem.entity.Room;

import java.util.List;

public record RoomRequestDTO(
        @NotNull(message = "Room name cannot be empty or just spaces")
        String name,
        @NotNull(message = "Capacity is required")
        Integer capacity,
        List<Long> equipmentIds,
        @NotNull(message = "Status is required")
        Room.Status status) {}
