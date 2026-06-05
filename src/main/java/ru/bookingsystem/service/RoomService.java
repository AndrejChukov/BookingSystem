package ru.bookingsystem.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookingsystem.entity.Room;
import ru.bookingsystem.repository.RoomRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

}
