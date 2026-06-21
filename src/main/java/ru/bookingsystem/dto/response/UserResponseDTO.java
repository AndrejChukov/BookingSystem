package ru.bookingsystem.dto.response;

import ru.bookingsystem.entity.User;

public record UserResponseDTO(Long id, String username, String email, User.Role role) {
}


