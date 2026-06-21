package ru.bookingsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.bookingsystem.dto.request.UserRequestDTO;
import ru.bookingsystem.dto.response.UserResponseDTO;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.mapper.UserMapper;
import ru.bookingsystem.repository.UserRepository;
import ru.bookingsystem.security.TokenService;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    public UserResponseDTO register(UserRequestDTO userRequest) {
        User newUser = userMapper.toEntity(userRequest);

        newUser.setPassword(passwordEncoder.encode(userRequest.password()));
        newUser.setRole(User.Role.USER);

        userRepository.save(newUser);

        String jwtToken = tokenService.generateToken(new UsernamePasswordAuthenticationToken(
                newUser,
                null,
                newUser.getAuthorities()
        ));

        return new UserResponseDTO(jwtToken, newUser.getUsername(), newUser.getEmail());
    }

    public UserResponseDTO authenticate(UserRequestDTO userRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userRequest.username(), userRequest.password()
                )
        );
        User authUser = (User) authentication.getPrincipal();

        String jwtToken = tokenService.generateToken(authentication);
        return new UserResponseDTO(jwtToken, authUser.getUsername(), authUser.getEmail());
    }
}
