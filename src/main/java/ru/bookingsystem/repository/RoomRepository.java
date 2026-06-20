package ru.bookingsystem.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bookingsystem.entity.Room;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    @EntityGraph(attributePaths = {"equipmentList"})
    List<Room> findAllByStatus(Room.Status status, Sort sort);

    @Override
    @EntityGraph(attributePaths = {"equipmentList"})
    List<Room> findAll(Sort sort);
}
