package ru.bookingsystem.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.bookingsystem.entity.Room;
import ru.bookingsystem.repository.RoomRepository;
import ru.bookingsystem.support.AbstractIntegrationTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerIntegrationTest extends AbstractIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired RoomRepository roomRepository;

    @Test
    void entityNotFound_shouldReturn404WithErrorResponseShape() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"username":"not-found-user","email":"not-found@example.com","password":"secret123"}
                                """))
                .andExpect(status().isOk());

        String token = ru.bookingsystem.support.SecurityTestHelper
                .obtainJwtToken(mockMvc, objectMapper, "not-found-user", "secret123");

        mockMvc.perform(get("/api/rooms/{id}", 999_999L)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Entity not found"));
    }

    @Test
    void validationError_shouldReturn400WithErrorResponseShape() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"\",\"email\":\"bad-email\",\"password\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Validation failed"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void invalidSortProperty_shouldReturn400() throws Exception {
        Room room = new Room();
        room.setName("Sort Room");
        room.setCapacity(3);
        room.setStatus(Room.Status.AVAILABLE);
        roomRepository.save(room);

        mockMvc.perform(get("/api/rooms/available")
                        .param("sortBy", "nonExistingField"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("wrong property"));
    }
}
