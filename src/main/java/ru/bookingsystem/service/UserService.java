package ru.bookingsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import ru.bookingsystem.dto.request.UserRequestDTO;
import ru.bookingsystem.dto.response.LoginRequestDTO;
import ru.bookingsystem.dto.response.UserResponseDTO;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.mapper.UserMapper;
import ru.bookingsystem.repository.UserRepository;
import ru.bookingsystem.security.TokenService;

import java.util.Map;

/**
 * Service that implements user-related business logic.
 *
 * <p>This service is responsible for:
 * - user registration with password encryption and JWT token generation
 * - user authentication with credentials validation and JWT token generation
 * - retrieving current authenticated user information from security context
 *
 * <p>It integrates with Spring Security for authentication, password encoding,
 * and JWT token management. User DTOs are converted to/from entities using UserMapper.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    /**
     * Registers a new user with provided credentials.
     *
     * <p>Creates a new user account with the provided username, email, and password.
     * The password is encrypted using PasswordEncoder. New users are assigned USER role.
     * A JWT token is generated and returned for immediate login.
     *
     * @param userRequest the registration data (username, email, password)
     * @return UserResponseDTO containing JWT token and user information
     * @throws org.springframework.dao.DataIntegrityViolationException if username/email already exists
     */
    public UserResponseDTO register(UserRequestDTO userRequest) {
        log.info("Registering new user username={}", userRequest.username());
        User newUser = userMapper.toEntity(userRequest);

        newUser.setPassword(passwordEncoder.encode(userRequest.password()));
        newUser.setRole(User.Role.USER);

        userRepository.save(newUser);

        String jwtToken = tokenService.generateToken(new UsernamePasswordAuthenticationToken(
                newUser,
                null,
                newUser.getAuthorities()
        ));

        log.debug("User registered username={} id={}", newUser.getUsername(), newUser.getId());
        return new UserResponseDTO(jwtToken, newUser.getUsername(), newUser.getEmail());
    }

    /**
     * Authenticates a user with username and password credentials.
     *
     * <p>Validates the provided credentials against stored user data using
     * AuthenticationManager. Upon successful authentication, a JWT token is generated.
     *
     * @param userRequest the login credentials (username, password)
     * @return UserResponseDTO containing JWT token and user information
     * @throws org.springframework.security.core.AuthenticationException if credentials are invalid
     */
    public UserResponseDTO authenticate(LoginRequestDTO userRequest) {
        log.info("Authenticating user username={}", userRequest.username());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userRequest.username(), userRequest.password()
                )
        );
        User authUser = (User) authentication.getPrincipal();

        String jwtToken = tokenService.generateToken(authentication);
        log.debug("User authenticated username={}", authUser.getUsername());
        return new UserResponseDTO(jwtToken, authUser.getUsername(), authUser.getEmail());
    }

    /**
     * Retrieves claims from the JWT token of the currently authenticated user.
     *
     * <p>Extracts the JWT token from the security context and returns all claims
     * contained within it (user id, username, roles, etc.).
     *
     * @return Map of JWT claims for the current authenticated user
     * @throws ru.bookingsystem.exception.EntityNotFoundException if no user is authenticated
     */
    public Map<String, Object> getUser() {
        log.debug("Fetching current authenticated user claims");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        return jwt.getClaims();
    }

}
