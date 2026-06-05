package ru.bookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.bookingsystem.entity.Room;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
