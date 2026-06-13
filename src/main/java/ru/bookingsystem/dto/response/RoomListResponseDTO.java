package ru.bookingsystem.dto.response;

import ru.bookingsystem.entity.Room;

import java.time.Instant;

public record RoomListResponseDTO(
        String name, int capacity, Room.Status status,
        Instant createdAt, Instant updatedAt) {}
