package ru.bookingsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.bookingsystem.dto.request.RoomRequestDTO;
import ru.bookingsystem.entity.Equipment;
import ru.bookingsystem.entity.Room;
import ru.bookingsystem.exception.EntityNotFoundException;
import ru.bookingsystem.mapper.RoomMapper;
import ru.bookingsystem.repository.EquipmentRepository;
import ru.bookingsystem.repository.RoomRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @Mock RoomRepository roomRepository;
    @Mock EquipmentRepository equipmentRepository;
    @Mock RoomMapper roomMapper;

    @Captor ArgumentCaptor<Room> roomCaptor;

    @InjectMocks RoomService roomService;

    private static final Long ROOM_ID = 1L;
    private Room room;
    private RoomRequestDTO roomRequest;
    private Equipment equipment;
    private List<Equipment> equipments;

    @BeforeEach
    void setUp() {
        room = new Room();
        room.setId(ROOM_ID);
        room.setName("Audience");
        room.setCreatedAt(Instant.now());
        room.setUpdatedAt(Instant.now());
        roomRequest = new RoomRequestDTO(
                "Audience", 20, List.of(1L, 2L), Room.Status.AVAILABLE);
        equipment = new Equipment();
        equipments = Collections.singletonList(equipment);
    }

    @Test
    void getAllRooms() {
        List<Room> rooms = Collections.singletonList(room);
        when(roomRepository.findAll()).thenReturn(rooms);

        List<Room> response = roomService.getAllRooms();

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(room, response.get(0));

        verify(roomRepository).findAll();
    }

    @Test
    void getRoomById_Success() {
        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(room));

        Room response = roomService.getRoomById(ROOM_ID);

        assertNotNull(response);
        assertEquals(room, response);
        verify(roomRepository).findById(ROOM_ID);
    }

    @Test
    void getRoomById_ShouldThrow_EntityNotFoundException() {
        when(roomRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roomService.getRoomById(ROOM_ID));

        verify(roomRepository).findById(ROOM_ID);
    }

    @Test
    void createRoom() {
        when(roomMapper.toEntity(roomRequest)).thenReturn(room);
        when(equipmentRepository.findAllById(anyList())).thenReturn(equipments);
        when(roomRepository.save(room)).thenReturn(room);

        Room response = roomService.createRoom(roomRequest);

        assertNotNull(response);
        assertEquals(room, response);
        assertEquals(equipments, room.getEquipmentList());

        verify(roomMapper).toEntity(any(RoomRequestDTO.class));
        verify(equipmentRepository).findAllById(anyList());
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void updateRoom_Success() {
        RoomRequestDTO updatedRoom =
                new RoomRequestDTO("updated name", 100, List.of(1L), Room.Status.AVAILABLE);

        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        roomService.updateRoom(updatedRoom, ROOM_ID);

        verify(roomRepository).save(roomCaptor.capture());
        Room capturedRoom = roomCaptor.getValue();

        assertNotNull(capturedRoom);
        assertEquals(ROOM_ID, capturedRoom.getId());

        verify(roomMapper).updateEntityFromDto(updatedRoom, room);
        verify(roomRepository).findById(anyLong());
        verify(roomRepository).save(any(Room.class));
    }

    @Test
    void updateRoom_ShouldThrow_EntityNotFoundException() {
        when(roomRepository.findById(ROOM_ID)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roomService.updateRoom(roomRequest, ROOM_ID));

        verify(roomRepository, never()).save(any(Room.class));
        verify(roomRepository).findById(anyLong());
    }

    @Test
    void deleteRoom_Success() {
        when(roomRepository.existsById(anyLong())).thenReturn(true);
        roomService.deleteRoom(ROOM_ID);

        verify(roomRepository).existsById(anyLong());
        verify(roomRepository).deleteById(anyLong());
    }

    @Test
    void deleteRoom_ShouldThrow_EntityNotFoundException() {
        when(roomRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> roomService.deleteRoom(ROOM_ID));

        verify(roomRepository).existsById(anyLong());
        verify(roomRepository, never()).deleteById(anyLong());
    }
}