package ru.bookingsystem.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import ru.bookingsystem.dto.request.UserRequestDTO;
import ru.bookingsystem.dto.response.LoginRequestDTO;
import ru.bookingsystem.dto.response.UserResponseDTO;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.mapper.UserMapper;
import ru.bookingsystem.repository.UserRepository;
import ru.bookingsystem.security.TokenService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    AuthenticationManager authenticationManager;
    @Mock
    TokenService tokenService;

    @InjectMocks
    UserService userService;

    private UserRequestDTO userRequest;
    private User userEntity;

    @BeforeEach
    void setUp() {
        userRequest = new UserRequestDTO("testuser", "test@domain.com", "secret");
        userEntity = new User();
        userEntity.setUsername("testuser");
        userEntity.setEmail("test@domain.com");
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void register_shouldSaveUserAndReturnJwtResponse() {
        when(userMapper.toEntity(userRequest)).thenReturn(userEntity);
        when(passwordEncoder.encode("secret")).thenReturn("hashed-secret");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(tokenService.generateToken(any())).thenReturn("jwt-token-value");

        UserResponseDTO response = userService.register(userRequest);

        assertNotNull(response);
        assertEquals("jwt-token-value", response.jwtToken());
        assertEquals("testuser", response.username());
        assertEquals("test@domain.com", response.email());

        verify(userMapper).toEntity(userRequest);
        verify(passwordEncoder).encode("secret");
        verify(userRepository).save(any(User.class));
        verify(tokenService).generateToken(any());
    }

    @Test
    void authenticate_shouldReturnJwtOnSuccessfulAuthentication() {
        LoginRequestDTO login = new LoginRequestDTO("testuser", "secret");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userEntity);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(tokenService.generateToken(authentication)).thenReturn("auth-jwt");

        UserResponseDTO response = userService.authenticate(login);

        assertNotNull(response);
        assertEquals("auth-jwt", response.jwtToken());
        assertEquals("testuser", response.username());
        assertEquals("test@domain.com", response.email());

        verify(authenticationManager).authenticate(any());
        verify(tokenService).generateToken(authentication);
    }

    @Test
    void getUser_shouldReturnJwtClaimsMapFromSecurityContext() {
        Jwt jwt = mock(Jwt.class);
        Map<String, Object> claims = Map.of("sub", "testuser", "email", "test@domain.com");
        when(jwt.getClaims()).thenReturn(claims);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        Map<String, Object> result = userService.getUser();

        assertNotNull(result);
        assertEquals(claims, result);
    }
}

