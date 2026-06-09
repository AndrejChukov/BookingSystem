package ru.bookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bookingsystem.entity.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
}
