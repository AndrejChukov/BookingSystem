package ru.bookingsystem.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.dto.request.UserRequestDTO;
import ru.bookingsystem.dto.response.LoginRequestDTO;
import ru.bookingsystem.dto.response.UserResponseDTO;
import ru.bookingsystem.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/auth/register")
    public UserResponseDTO register(@Valid @RequestBody UserRequestDTO userRequest) {
        log.info("POST /api/auth/register username={}", userRequest.username());
        return userService.register(userRequest);
    }

    @PostMapping("/auth/signin")
    public UserResponseDTO authenticate(@Valid @RequestBody LoginRequestDTO userRequest) {
        log.info("POST /api/auth/signin username={}", userRequest.username());
        return userService.authenticate(userRequest);
    }

    @GetMapping("/user")
    public Map<String, Object> getUser() {
        log.info("GET /api/user current user info requested");
        return userService.getUser();
    }

}
