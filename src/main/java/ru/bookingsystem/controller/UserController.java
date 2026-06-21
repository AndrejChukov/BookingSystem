package ru.bookingsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.bookingsystem.dto.request.UserRequestDTO;
import ru.bookingsystem.dto.response.UserResponseDTO;
import ru.bookingsystem.service.UserService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/register")
    public UserResponseDTO register(@Valid @RequestBody UserRequestDTO userRequest) {
        return userService.register(userRequest);
    }

    @PostMapping("/auth/signin")
    public UserResponseDTO authenticate(@Valid @RequestBody UserRequestDTO userRequest) {
        return userService.authenticate(userRequest);
    }

}
