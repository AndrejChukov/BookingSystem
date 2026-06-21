package ru.bookingsystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import ru.bookingsystem.entity.User;

public record UserRequestDTO(
        @NotBlank(message = "Username is required")
        String username,
        @Email(message = "Email should be valid")
        @NotBlank(message = "Email is required")
        String email,
        @NotBlank(message = "Password is required")
        String password,
        User.Role role
) {}


