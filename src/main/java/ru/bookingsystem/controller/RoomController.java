package ru.bookingsystem.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.entity.Room;
import ru.bookingsystem.service.RoomService;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class RoomController {

    private final RoomService roomService;

    @GetMapping("/rooms")
    public List<Room> getAllRooms() {
        return roomService.getAllRooms();
    }

    @PostMapping("/room")
    public Room createRoom(@RequestBody Room room) {
        return roomService.createRoom(room);
    }

}
