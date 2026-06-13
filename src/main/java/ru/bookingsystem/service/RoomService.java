package ru.bookingsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bookingsystem.dto.request.RoomRequestDTO;
import ru.bookingsystem.entity.Equipment;
import ru.bookingsystem.entity.Room;
import ru.bookingsystem.exception.EntityNotFoundException;
import ru.bookingsystem.mapper.RoomMapper;
import ru.bookingsystem.repository.EquipmentRepository;
import ru.bookingsystem.repository.RoomRepository;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final EquipmentRepository equipmentRepository;
    private final RoomMapper roomMapper;

    @Transactional(readOnly = true)
    public List<Room> getAllRoomsSorted(Room.Status status, String sortedBy, String direction) {
        Sort.Direction dir = (direction.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(dir, sortedBy);

        if (status == null) {
            return roomRepository.findAll(sort);
        }

        return roomRepository.findAllByStatus(status, sort);
    }

    @Transactional(readOnly = true)
    public Room getRoomById(Long id) {
        return roomRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Room with ID: " + id + " not found"));
    }

    @Transactional
    public Room createRoom(RoomRequestDTO roomRequest) {
        Room newRoom = roomMapper.toEntity(roomRequest);
        assignEquipment(roomRequest, newRoom);
        return roomRepository.save(newRoom);
    }

    @Transactional
    public void updateRoom(RoomRequestDTO roomRequest, Long id) {
        Room existingRoom = roomRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Room with ID: " + id + " not found"));

        roomMapper.updateEntityFromDto(roomRequest, existingRoom);
        existingRoom.setUpdatedAt(Instant.now());

        assignEquipment(roomRequest, existingRoom);

        roomRepository.save(existingRoom);
    }

    @Transactional
    public void deleteRoom(Long id) {
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Room with ID: " + id + " not found");
        }
    }

    private void assignEquipment(RoomRequestDTO roomRequest, Room newRoom) {
        if (roomRequest.equipmentIds() != null && !roomRequest.equipmentIds().isEmpty()) {
            List<Equipment> equipmentList = equipmentRepository.findAllById(roomRequest.equipmentIds());
            newRoom.setEquipmentList(equipmentList);
        }
    }

}
