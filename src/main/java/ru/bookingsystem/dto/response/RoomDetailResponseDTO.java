package ru.bookingsystem.dto.response;

import ru.bookingsystem.entity.Equipment;
import ru.bookingsystem.entity.Room;

import java.time.Instant;
import java.util.List;

public record RoomDetailResponseDTO(
        String name, int capacity, Room.Status status,
        List<Equipment> equipmentList,
        Instant createdAt, Instant updatedAt) {
}
