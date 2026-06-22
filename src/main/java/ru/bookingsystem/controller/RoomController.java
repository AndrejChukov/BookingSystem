package ru.bookingsystem.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
// ...existing code...
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.dto.request.RoomRequestDTO;
import ru.bookingsystem.dto.response.RoomDetailResponseDTO;
import ru.bookingsystem.dto.response.RoomListResponseDTO;
import ru.bookingsystem.entity.Room;
import ru.bookingsystem.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/rooms")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public List<RoomListResponseDTO> getAllRooms(
            @RequestParam(required = false) Room.Status status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        log.info("GET /api/rooms status={} sortBy={} direction={}", status, sortBy, direction);
        return roomService.getAllRoomsSorted(status, sortBy, direction);
    }

    @GetMapping("/rooms/available")
    public List<RoomListResponseDTO> getAvailableRooms(
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        log.info("GET /api/rooms/available sortBy={} direction={}", sortBy, direction);
        return roomService.getAllRoomsSorted(Room.Status.AVAILABLE, sortBy, direction);
    }

    @GetMapping("/room/{id}")
    public RoomDetailResponseDTO getRoomById(@PathVariable("id") Long id) {
        log.info("GET /api/room/{}", id);
        return roomService.getRoomById(id);
    }

    @PostMapping("/room")
    @PreAuthorize("hasRole('ADMIN')")
    public RoomDetailResponseDTO createRoom(@Valid @RequestBody RoomRequestDTO roomRequest) {
        log.info("POST /api/room request={}", roomRequest);
        return roomService.createRoom(roomRequest);
    }

    @PutMapping("/room/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public void updateRoom(@Valid @RequestBody RoomRequestDTO roomRequest, @PathVariable("id") Long id) {
        log.info("PUT /api/room/{} request={}", id, roomRequest);
        roomService.updateRoom(roomRequest, id);
    }

    @DeleteMapping("/room/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteRoom(@PathVariable Long id) {
        log.info("DELETE /api/room/{}", id);
        roomService.deleteRoom(id);
    }

}
