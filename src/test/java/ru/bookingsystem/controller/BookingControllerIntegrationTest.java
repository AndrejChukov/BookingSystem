package ru.bookingsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import ru.bookingsystem.entity.Room;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.repository.RoomRepository;
import ru.bookingsystem.repository.UserRepository;
import ru.bookingsystem.support.AbstractIntegrationTest;
import ru.bookingsystem.support.BookingTestTimes;
import ru.bookingsystem.support.SecurityTestHelper;

import java.time.Instant;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookingControllerIntegrationTest extends AbstractIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired RoomRepository roomRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @Test
    void createAndGetMyBookings_shouldWorkForAuthenticatedUser() throws Exception {
        Room room = persistRoom();
        User user = persistUser("booking-user", "booking-user@example.com", User.Role.USER);
        String token = SecurityTestHelper.obtainJwtToken(mockMvc, objectMapper, user.getUsername(), "password");

        Instant start = BookingTestTimes.workingStart();
        Instant end = BookingTestTimes.workingEnd(start);

        mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"roomId":%d,"startTime":"%s","endTime":"%s"}
                                """.formatted(room.getId(), start, end)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.bookingStatus").value("CONFIRMED"));

        mockMvc.perform(get("/api/bookings/my")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void createBooking_shouldReturn400_whenValidationFails() throws Exception {
        User user = persistUser("invalid-booking-user", "invalid-booking@example.com", User.Role.USER);
        String token = SecurityTestHelper.obtainJwtToken(mockMvc, objectMapper, user.getUsername(), "password");

        mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"roomId\":null,\"startTime\":null,\"endTime\":null}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void cancelBooking_shouldCancelOwnedBooking() throws Exception {
        Room room = persistRoom();
        User user = persistUser("cancel-user", "cancel-user@example.com", User.Role.USER);
        String token = SecurityTestHelper.obtainJwtToken(mockMvc, objectMapper, user.getUsername(), "password");

        Instant start = BookingTestTimes.workingStart();
        Instant end = BookingTestTimes.workingEnd(start);

        String createResponse = mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"roomId":%d,"startTime":"%s","endTime":"%s"}
                                """.formatted(room.getId(), start, end)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long bookingId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(put("/api/bookings/cancel/{id}", bookingId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void deleteBooking_shouldReturn403_forRegularUser() throws Exception {
        User user = persistUser("delete-user", "delete-user@example.com", User.Role.USER);
        String token = SecurityTestHelper.obtainJwtToken(mockMvc, objectMapper, user.getUsername(), "password");

        mockMvc.perform(delete("/api/bookings/{id}", 1L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("Access denied"));
    }

    private Room persistRoom() {
        Room room = new Room();
        room.setName("Controller Room");
        room.setCapacity(8);
        room.setStatus(Room.Status.AVAILABLE);
        return roomRepository.save(room);
    }

    private User persistUser(String username, String email, User.Role role) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(role);
        return userRepository.save(user);
    }
}
