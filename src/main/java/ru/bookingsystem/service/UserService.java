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

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

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

    public Map<String, Object> getUser() {
        log.debug("Fetching current authenticated user claims");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Jwt jwt = (Jwt) auth.getPrincipal();
        return jwt.getClaims();
    }
}
