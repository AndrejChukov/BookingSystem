package ru.bookingsystem.security;

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
import ru.bookingsystem.support.SecurityTestHelper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityIntegrationTest extends AbstractIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired UserRepository userRepository;
    @Autowired RoomRepository roomRepository;
    @Autowired PasswordEncoder passwordEncoder;

    @Test
    void publicEndpoints_shouldBeAccessibleWithoutAuth() throws Exception {
        mockMvc.perform(get("/api/rooms/available"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoints_shouldReturn401WithoutAuth() throws Exception {
        mockMvc.perform(get("/api/bookings/my"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminOnlyRoomList_shouldReturn403_forRegularUser() throws Exception {
        User user = new User();
        user.setUsername("security-user");
        user.setEmail("security-user@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(User.Role.USER);
        userRepository.save(user);

        String token = SecurityTestHelper.obtainJwtToken(mockMvc, objectMapper, user.getUsername(), "password");

        mockMvc.perform(get("/api/rooms")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminOnlyRoomCreate_shouldReturn403_forWorker() throws Exception {
        User worker = new User();
        worker.setUsername("worker-user");
        worker.setEmail("worker-user@example.com");
        worker.setPassword(passwordEncoder.encode("password"));
        worker.setRole(User.Role.WORKER);
        userRepository.save(worker);

        String token = SecurityTestHelper.obtainJwtToken(mockMvc, objectMapper, worker.getUsername(), "password");

        mockMvc.perform(post("/api/rooms")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Forbidden Room","capacity":5,"status":"AVAILABLE"}
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void adminRoomCreate_shouldSucceed_forAdmin() throws Exception {
        User admin = new User();
        admin.setUsername("security-admin");
        admin.setEmail("security-admin@example.com");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setRole(User.Role.ADMIN);
        userRepository.save(admin);

        String token = SecurityTestHelper.obtainJwtToken(mockMvc, objectMapper, admin.getUsername(), "password");

        mockMvc.perform(post("/api/rooms")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Admin Room","capacity":12,"status":"AVAILABLE"}
                                """))
                .andExpect(status().isOk());
    }

    @Test
    void equipmentEndpoints_shouldReturn403_forUnauthenticatedRequests() throws Exception {
        mockMvc.perform(get("/api/equipment"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void roomDetail_shouldRequireAuthentication() throws Exception {
        Room room = new Room();
        room.setName("Protected Room");
        room.setCapacity(4);
        room.setStatus(Room.Status.AVAILABLE);
        room = roomRepository.save(room);

        mockMvc.perform(get("/api/rooms/{id}", room.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void deleteBooking_shouldReturn403_forRegularUser() throws Exception {
        User user = new User();
        user.setUsername("delete-security-user");
        user.setEmail("delete-security@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setRole(User.Role.USER);
        userRepository.save(user);

        String token = SecurityTestHelper.obtainJwtToken(mockMvc, objectMapper, user.getUsername(), "password");

        mockMvc.perform(delete("/api/bookings/{id}", 1L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
