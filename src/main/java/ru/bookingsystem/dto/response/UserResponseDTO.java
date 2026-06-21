package ru.bookingsystem.dto.response;


public record UserResponseDTO(String jwtToken, String username, String email) {
}


