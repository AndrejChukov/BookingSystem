package ru.bookingsystem.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.bookingsystem.dto.request.RoomRequestDTO;
import ru.bookingsystem.dto.response.RoomDetailResponseDTO;
import ru.bookingsystem.dto.response.RoomListResponseDTO;
import ru.bookingsystem.entity.BaseEntity;
import ru.bookingsystem.entity.Equipment;
import ru.bookingsystem.entity.Room;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RoomMapperTest {

    private RoomMapper mapper;

    @BeforeEach
    void setUp() {
        RoomMapperImpl roomMapper = new RoomMapperImpl();
        // RoomMapper uses EquipmentMapper (componentModel = spring, field injection),
        // so we wire the real implementation manually.
        ReflectionTestUtils.setField(roomMapper, "equipmentMapper", new EquipmentMapperImpl());
        this.mapper = roomMapper;
    }

    @Test
    void toEntity_shouldMapScalarFields() {
        RoomRequestDTO request = new RoomRequestDTO("Room A", 8, List.of(1L), Room.Status.AVAILABLE);

        Room entity = mapper.toEntity(request);

        assertEquals("Room A", entity.getName());
        assertEquals(8, entity.getCapacity());
        assertEquals(Room.Status.AVAILABLE, entity.getStatus());
    }

    @Test
    void toEntity_shouldReturnNull_whenRequestIsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void toRoomListResponseDTO_shouldMapFieldsIncludingAuditTimestamps() {
        Instant created = Instant.now().minusSeconds(120);
        Instant updated = Instant.now();
        Room room = new Room();
        room.setName("Room B");
        room.setCapacity(4);
        room.setStatus(Room.Status.OCCUPIED);
        ReflectionTestUtils.setField(room, "createdAt", created);
        ReflectionTestUtils.setField(room, "updatedAt", updated);

        RoomListResponseDTO dto = mapper.toRoomListResponseDTO(room);

        assertEquals("Room B", dto.name());
        assertEquals(4, dto.capacity());
        assertEquals(Room.Status.OCCUPIED, dto.status());
        assertEquals(created, dto.createdAt());
        assertEquals(updated, dto.updatedAt());
    }

    @Test
    void toRoomDetailResponseDTO_shouldMapEquipmentViaEquipmentMapper() {
        Equipment equipment = new Equipment();
        equipment.setId(3L);
        equipment.setName("TV");

        Room room = new Room();
        room.setName("Room C");
        room.setCapacity(12);
        room.setStatus(Room.Status.AVAILABLE);
        room.setEquipmentList(List.of(equipment));

        RoomDetailResponseDTO dto = mapper.toRoomDetailResponseDTO(room);

        assertEquals("Room C", dto.name());
        assertEquals(1, dto.equipmentList().size());
        assertEquals(3L, dto.equipmentList().get(0).id());
        assertEquals("TV", dto.equipmentList().get(0).name());
    }

    @Test
    void toRoomDetailResponseDTO_shouldHandleNullEquipmentList() {
        Room room = new Room();
        room.setName("Room D");
        room.setCapacity(2);
        room.setStatus(Room.Status.MAINTENANCE);

        RoomDetailResponseDTO dto = mapper.toRoomDetailResponseDTO(room);

        assertNull(dto.equipmentList());
    }

    @Test
    void updateEntityFromDto_shouldOverwriteProvidedFields() {
        Room existing = new Room();
        existing.setName("Old");
        existing.setCapacity(1);
        existing.setStatus(Room.Status.MAINTENANCE);

        mapper.updateEntityFromDto(
                new RoomRequestDTO("Updated", 20, null, Room.Status.AVAILABLE), existing);

        assertEquals("Updated", existing.getName());
        assertEquals(20, existing.getCapacity());
        assertEquals(Room.Status.AVAILABLE, existing.getStatus());
    }

    @Test
    void updateEntityFromDto_shouldIgnoreNullNameAndStatus() {
        Room existing = new Room();
        existing.setName("Keep");
        existing.setCapacity(5);
        existing.setStatus(Room.Status.OCCUPIED);

        mapper.updateEntityFromDto(new RoomRequestDTO(null, 9, null, null), existing);

        assertEquals("Keep", existing.getName());
        assertEquals(9, existing.getCapacity());
        assertEquals(Room.Status.OCCUPIED, existing.getStatus());
    }
}
