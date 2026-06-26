package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.dto.request.UserRequestDTO;
import ru.bookingsystem.dto.response.LoginRequestDTO;
import ru.bookingsystem.dto.response.UserResponseDTO;
import ru.bookingsystem.service.UserService;

import java.util.Map;

/**
 * REST controller that exposes user authentication and profile endpoints.
 *
 * <p>This controller manages user-related operations including registration, authentication,
 * and user information retrieval. All endpoints are public or require standard authentication.
 *
 * <p>Endpoints:
 * - POST   /api/auth/register : register new user (public)
 * - POST   /api/auth/signin   : authenticate user and retrieve JWT token (public)
 * - GET    /api/user          : retrieve current authenticated user information (requires authentication)
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Authentication", description = "Operations for user registration, authentication and profile management")
public class UserController {

    private final UserService userService;

    /**
     * Registers a new user.
     *
     * <p>Public endpoint (no authentication required). Accepts user registration data,
     * validates it, creates new user with USER role, and returns JWT token for immediate login.
     *
     * @param userRequest the registration data (username, email, password)
     * @return UserResponseDTO containing JWT token and user information
     * @throws ru.bookingsystem.exception.BadRequestParametersException if validation fails
     */
    @PostMapping(value = "/auth/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Register new user", description = "Create new user account and return JWT token (public endpoint)",
            responses = {@ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content),
                    @ApiResponse(responseCode = "400", description = "Invalid registration data")})
    public UserResponseDTO register(@Valid @RequestBody UserRequestDTO userRequest) {
        log.info("POST /api/auth/register username={}", userRequest.username());
        return userService.register(userRequest);
    }

    /**
     * Authenticates user and returns JWT token.
     *
     * <p>Public endpoint (no authentication required). Validates username and password against
     * stored credentials and returns JWT token upon successful authentication.
     *
     * @param userRequest the login credentials (username, password)
     * @return UserResponseDTO containing JWT token and user information
     * @throws org.springframework.security.core.AuthenticationException if credentials are invalid
     */
    @PostMapping(value = "/auth/signin", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Authenticate user", description = "Sign in with credentials and retrieve JWT token (public endpoint)",
            responses = {@ApiResponse(responseCode = "200", description = "Authentication successful", content = @Content),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials")})
    public UserResponseDTO authenticate(@Valid @RequestBody LoginRequestDTO userRequest) {
        log.info("POST /api/auth/signin username={}", userRequest.username());
        return userService.authenticate(userRequest);
    }

    /**
     * Retrieves current authenticated user information.
     *
     * <p>Requires authentication (valid JWT token in Authorization header).
     * Returns user claims from the JWT token (user id, username, roles, etc.).
     *
     * @return Map containing JWT claims for the current authenticated user
     * @throws ru.bookingsystem.exception.EntityNotFoundException if user not authenticated
     */
    @GetMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get current user", description = "Retrieve information about currently authenticated user",
            responses = {@ApiResponse(responseCode = "200", description = "User information", content = @Content),
                    @ApiResponse(responseCode = "401", description = "User not authenticated")})
    public Map<String, Object> getUser() {
        log.info("GET /api/user current user info requested");
        return userService.getUser();
    }

}
