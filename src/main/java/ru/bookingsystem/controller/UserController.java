package ru.bookingsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.dto.request.UserRequestDTO;
import ru.bookingsystem.dto.response.LoginRequestDTO;
import ru.bookingsystem.dto.response.UserResponseDTO;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.service.UserService;

import java.util.Map;

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
    public UserResponseDTO authenticate(@Valid @RequestBody LoginRequestDTO userRequest) {
        return userService.authenticate(userRequest);
    }

    @GetMapping("/user")
    public Map<String, Object> getUser() {
        return userService.getUser();
    }

}
