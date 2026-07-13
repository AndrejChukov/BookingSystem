package ru.bookingsystem.mapper;

import org.junit.jupiter.api.Test;
import ru.bookingsystem.dto.request.UserRequestDTO;
import ru.bookingsystem.dto.response.UserResponseDTO;
import ru.bookingsystem.entity.User;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserMapperTest {

    private final UserMapper mapper = new UserMapperImpl();

    @Test
    void toEntity_shouldMapUsernameEmailAndPassword() {
        User entity = mapper.toEntity(new UserRequestDTO("john", "john@example.com", "secret"));

        assertEquals("john", entity.getUsername());
        assertEquals("john@example.com", entity.getEmail());
        assertEquals("secret", entity.getPassword());
        assertNull(entity.getRole(), "role must not be assigned by the mapper");
    }

    @Test
    void toEntity_shouldReturnNull_whenRequestIsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void toResponse_shouldMapUsernameAndEmail() {
        User user = new User();
        user.setUsername("jane");
        user.setEmail("jane@example.com");
        user.setPassword("hashed");

        UserResponseDTO response = mapper.toResponse(user);

        assertEquals("jane", response.username());
        assertEquals("jane@example.com", response.email());
        assertNull(response.jwtToken(), "jwtToken is not sourced from the entity");
    }

    @Test
    void toResponse_shouldReturnNull_whenUserIsNull() {
        assertNull(mapper.toResponse(null));
    }

    @Test
    void updateEntityFromDto_shouldOverwriteProvidedFields() {
        User existing = new User();
        existing.setUsername("old");
        existing.setEmail("old@example.com");
        existing.setPassword("old-pass");
        existing.setRole(User.Role.ADMIN);

        mapper.updateEntityFromDto(new UserRequestDTO("new", "new@example.com", "new-pass"), existing);

        assertEquals("new", existing.getUsername());
        assertEquals("new@example.com", existing.getEmail());
        assertEquals("new-pass", existing.getPassword());
        assertEquals(User.Role.ADMIN, existing.getRole(), "role must be preserved on update");
    }

    @Test
    void updateEntityFromDto_shouldIgnoreNullFields() {
        User existing = new User();
        existing.setUsername("keep");
        existing.setEmail("keep@example.com");
        existing.setPassword("keep-pass");

        mapper.updateEntityFromDto(new UserRequestDTO(null, null, null), existing);

        assertEquals("keep", existing.getUsername());
        assertEquals("keep@example.com", existing.getEmail());
        assertEquals("keep-pass", existing.getPassword());
    }
}
