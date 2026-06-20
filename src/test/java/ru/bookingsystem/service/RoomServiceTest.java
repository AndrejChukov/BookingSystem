package ru.bookingsystem.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import ru.bookingsystem.dto.request.RoomRequestDTO;
import ru.bookingsystem.dto.response.RoomDetailResponseDTO;
import ru.bookingsystem.dto.response.RoomListResponseDTO;
import ru.bookingsystem.entity.Equipment;
import ru.bookingsystem.entity.Room;
import ru.bookingsystem.exception.EntityNotFoundException;
import ru.bookingsystem.mapper.RoomMapper;
import ru.bookingsystem.repository.EquipmentRepository;
import ru.bookingsystem.repository.RoomRepository;

import java.time.Instant;
import java.util.Collections;
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
    private RoomDetailResponseDTO mockRoomDetailedResponse;

    @BeforeEach
    void setUp() {
        equipment = new Equipment();
        equipments = Collections.singletonList(equipment);

        room = new Room();
        room.setId(ROOM_ID);
        room.setName("Audience");
        room.setEquipmentList(equipments);
        roomRequest = new RoomRequestDTO(
                "Audience", 20, List.of(1L), Room.Status.AVAILABLE);
        mockRoomDetailedResponse = new RoomDetailResponseDTO(
                "Test", 27,  Room.Status.AVAILABLE,
                null, Instant.now(), Instant.now()
        );
    }

    @Test
    void getAllRooms() {
        RoomListResponseDTO expectedRoom = new RoomListResponseDTO(
                "Test", 10, Room.Status.AVAILABLE,
                Instant.now(), Instant.now());

        List<Room> rooms = Collections.singletonList(room);
        when(roomRepository.findAllByStatus(any(Room.Status.class), any(Sort.class))).thenReturn(rooms);
        when(roomMapper.toRoomListResponseDTO(any(Room.class))).thenReturn(expectedRoom);

        List<RoomListResponseDTO> response =
                roomService.getAllRoomsSorted(Room.Status.AVAILABLE, "createdAt", "desc");

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals(expectedRoom, response.get(0));

        verify(roomRepository).findAllByStatus(Room.Status.AVAILABLE,
                        Sort.by(Sort.Direction.DESC, "createdAt"));
        verify(roomMapper).toRoomListResponseDTO(room);
    }

    @Test
    void getRoomById_Success() {
        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(room));
        when(roomMapper.toRoomDetailResponseDTO(any(Room.class))).thenReturn(mockRoomDetailedResponse);

        RoomDetailResponseDTO response = roomService.getRoomById(ROOM_ID);

        assertNotNull(response);
        assertEquals(mockRoomDetailedResponse, response);

        verify(roomRepository).findById(ROOM_ID);
        verify(roomMapper).toRoomDetailResponseDTO(room);
    }

    @Test
    void getRoomById_ShouldThrow_EntityNotFoundException() {
        when(roomRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roomService.getRoomById(ROOM_ID));

        verify(roomRepository).findById(ROOM_ID);
    }

    @Test
    void createRoom() {
        when(roomMapper.toEntity(any(RoomRequestDTO.class))).thenReturn(room);
        when(equipmentRepository.findAllById(anyList())).thenReturn(equipments);
        when(roomRepository.save(any(Room.class))).thenReturn(room);
        when(roomMapper.toRoomDetailResponseDTO(any(Room.class))).thenReturn(mockRoomDetailedResponse);

        RoomDetailResponseDTO response = roomService.createRoom(roomRequest);

        assertNotNull(response);
        assertEquals(mockRoomDetailedResponse, response);

        verify(roomRepository).save(roomCaptor.capture());
        Room capturedRoom = roomCaptor.getValue();

        assertEquals(room.getName(), capturedRoom.getName());
        assertEquals(equipments, capturedRoom.getEquipmentList());

        verify(roomMapper).toEntity(roomRequest);
        verify(equipmentRepository).findAllById(anyList());
    }

    @Test
    void updateRoom_Success() {
        RoomRequestDTO updatedRoom =
                new RoomRequestDTO("updated name", 100, List.of(1L), Room.Status.AVAILABLE);

        when(roomRepository.findById(anyLong())).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);
        when(equipmentRepository.findAllById(anyList())).thenReturn(equipments);

        roomService.updateRoom(updatedRoom, ROOM_ID);

        verify(roomRepository).save(roomCaptor.capture());
        Room capturedRoom = roomCaptor.getValue();

        assertNotNull(capturedRoom);
        assertEquals(ROOM_ID, capturedRoom.getId());

        verify(roomMapper).updateEntityFromDto(updatedRoom, room);
        verify(roomRepository).findById(ROOM_ID);
        verify(roomRepository).save(room);
    }

    @Test
    void updateRoom_ShouldThrow_EntityNotFoundException() {
        when(roomRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> roomService.updateRoom(roomRequest, ROOM_ID));

        verify(roomRepository, never()).save(any(Room.class));
        verify(roomRepository).findById(ROOM_ID);
    }

    @Test
    void deleteRoom_Success() {
        when(roomRepository.existsById(anyLong())).thenReturn(true);
        roomService.deleteRoom(ROOM_ID);

        verify(roomRepository).existsById(ROOM_ID);
        verify(roomRepository).deleteById(ROOM_ID);
    }

    @Test
    void deleteRoom_ShouldThrow_EntityNotFoundException() {
        when(roomRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> roomService.deleteRoom(ROOM_ID));

        verify(roomRepository).existsById(ROOM_ID);
        verify(roomRepository, never()).deleteById(anyLong());
    }
}