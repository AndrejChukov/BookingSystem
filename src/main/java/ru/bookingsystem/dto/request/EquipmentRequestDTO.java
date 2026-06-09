package ru.bookingsystem.dto.request;

import jakarta.validation.constraints.NotBlank;

public record EquipmentRequestDTO(
        @NotBlank(message = "Equipment name cannot be empty or just spaces")
        String name) {}
