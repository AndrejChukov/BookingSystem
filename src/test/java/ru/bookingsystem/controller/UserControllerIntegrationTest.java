package ru.bookingsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.repository.UserRepository;
import ru.bookingsystem.support.AbstractIntegrationTest;
import ru.bookingsystem.support.SecurityTestHelper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @Test
    void register_shouldReturnJwtToken() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"new-user","email":"new-user@example.com","password":"secret123"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jwtToken").isNotEmpty())
                .andExpect(jsonPath("$.username").value("new-user"));
    }

    @Test
    void signin_shouldReturn401_forInvalidCredentials() throws Exception {
        User user = new User();
        user.setUsername("known-user");
        user.setEmail("known-user@example.com");
        user.setPassword(passwordEncoder.encode("correct-password"));
        user.setRole(User.Role.USER);
        userRepository.save(user);

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"known-user","password":"wrong-password"}
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Authentication failed"));
    }

    @Test
    void getUser_shouldReturnClaims_forAuthenticatedUser() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"profile-user","email":"profile-user@example.com","password":"secret123"}
                                """))
                .andExpect(status().isOk());

        String token = SecurityTestHelper.obtainJwtToken(mockMvc, objectMapper, "profile-user", "secret123");

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("profile-user"))
                .andExpect(jsonPath("$.roles").isArray());
    }
}
