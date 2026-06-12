package ru.bookingsystem.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.dto.request.RoomRequestDTO;
import ru.bookingsystem.entity.Room;
import ru.bookingsystem.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/rooms")
    public List<Room> getAllRooms(
            @RequestParam(required = false) Room.Status status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction
    ) {
        return roomService.getAllRoomsSorted(status, sortBy, direction);
    }

    @GetMapping("/room/{id}")
    public Room getRoomById(@PathVariable("id") Long id) {
        return roomService.getRoomById(id);
    }

    @PostMapping("/room")
    public Room createRoom(@Valid @RequestBody RoomRequestDTO roomRequest) {
        return roomService.createRoom(roomRequest);
    }

    @PutMapping("/room/{id}")
    public void updateRoom(@Valid @RequestBody RoomRequestDTO roomRequest, @PathVariable("id") Long id) {
        roomService.updateRoom(roomRequest, id);
    }

    @DeleteMapping("/room/{id}")
    public void deleteRoom(@PathVariable Long id) {
        roomService.deleteRoom(id);
    }

}
