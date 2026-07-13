package ru.bookingsystem.mapper;

import org.junit.jupiter.api.Test;
import ru.bookingsystem.dto.request.EquipmentRequestDTO;
import ru.bookingsystem.dto.response.EquipmentResponseDTO;
import ru.bookingsystem.entity.Equipment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EquipmentMapperTest {

    private final EquipmentMapper mapper = new EquipmentMapperImpl();

    @Test
    void toEntity_shouldMapName() {
        Equipment entity = mapper.toEntity(new EquipmentRequestDTO("Projector"));

        assertEquals("Projector", entity.getName());
        assertNull(entity.getId(), "id must not be set from the request");
    }

    @Test
    void toEntity_shouldReturnNull_whenRequestIsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void equipmentToResponse_shouldMapIdAndName() {
        Equipment equipment = new Equipment();
        equipment.setId(11L);
        equipment.setName("Whiteboard");

        EquipmentResponseDTO response = mapper.equipmentToResponse(equipment);

        assertEquals(11L, response.id());
        assertEquals("Whiteboard", response.name());
    }

    @Test
    void equipmentToResponse_shouldReturnNull_whenEquipmentIsNull() {
        assertNull(mapper.equipmentToResponse(null));
    }

    @Test
    void updateEntityFromDto_shouldOverwriteName() {
        Equipment existing = new Equipment();
        existing.setId(5L);
        existing.setName("Old Name");

        mapper.updateEntityFromDto(new EquipmentRequestDTO("New Name"), existing);

        assertEquals("New Name", existing.getName());
        assertEquals(5L, existing.getId(), "id must be preserved");
    }

    @Test
    void updateEntityFromDto_shouldKeepExistingName_whenDtoNameIsNull() {
        Equipment existing = new Equipment();
        existing.setName("Keep Me");

        mapper.updateEntityFromDto(new EquipmentRequestDTO(null), existing);

        assertEquals("Keep Me", existing.getName(), "null values must be ignored on update");
    }
}
