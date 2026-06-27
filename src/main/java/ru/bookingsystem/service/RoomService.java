package ru.bookingsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bookingsystem.dto.request.RoomRequestDTO;
import ru.bookingsystem.dto.response.RoomDetailResponseDTO;
import ru.bookingsystem.dto.response.RoomListResponseDTO;
import ru.bookingsystem.entity.Equipment;
import ru.bookingsystem.entity.Room;
import ru.bookingsystem.exception.EntityNotFoundException;
import ru.bookingsystem.mapper.RoomMapper;
import ru.bookingsystem.repository.EquipmentRepository;
import ru.bookingsystem.repository.RoomRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final EquipmentRepository equipmentRepository;
    private final RoomMapper roomMapper;

    @Transactional(readOnly = true)
    @Cacheable(value = "rooms", key = "{#status, #sortedBy, #direction}")
    public List<RoomListResponseDTO> getAllRoomsSorted(Room.Status status, String sortedBy, String direction) {
        log.debug("Fetching all rooms sorted status={} sortBy={} direction={}", status, sortedBy, direction);
        Sort.Direction dir = (direction.equalsIgnoreCase("desc")) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = Sort.by(dir, sortedBy);

        if (status == null) {
            return roomRepository.findAll(sort).stream()
                    .map(roomMapper::toRoomListResponseDTO)
                    .toList();
        }

        return roomRepository.findAllByStatus(status, sort).stream()
                .map(roomMapper::toRoomListResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "rooms", key = "#id")
    public RoomDetailResponseDTO getRoomById(Long id) {
        log.debug("Fetching room by id={}", id);
        return roomMapper.toRoomDetailResponseDTO(roomRepository.findById(id)
                .orElseThrow(() ->
                new EntityNotFoundException("Room with ID: " + id + " not found")));
    }

    @Transactional
    @CacheEvict(value = "rooms", allEntries = true)
    public RoomDetailResponseDTO createRoom(RoomRequestDTO roomRequest) {
        log.info("Creating room: {}", roomRequest);
        Room newRoom = roomMapper.toEntity(roomRequest);
        assignEquipment(roomRequest, newRoom);
        Room saved = roomRepository.save(newRoom);
        log.debug("Created room id={}", saved.getId());
        return roomMapper.toRoomDetailResponseDTO(saved);
    }

    @Transactional
    @CacheEvict(value = "rooms", allEntries = true)
    public void updateRoom(RoomRequestDTO roomRequest, Long id) {
        log.info("Updating room id={} with {}", id, roomRequest);
        Room existingRoom = roomRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Room with ID: " + id + " not found"));

        roomMapper.updateEntityFromDto(roomRequest, existingRoom);

        assignEquipment(roomRequest, existingRoom);

        roomRepository.save(existingRoom);
    }

    @Transactional
    @CacheEvict(value = "rooms", allEntries = true)
    public void deleteRoom(Long id) {
        log.info("Deleting room id={}", id);
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Room with ID: " + id + " not found");
        }
    }

    private void assignEquipment(RoomRequestDTO roomRequest, Room room) {
        if (roomRequest.equipmentIds() != null && !roomRequest.equipmentIds().isEmpty()) {
            List<Equipment> equipmentList = equipmentRepository.findAllById(roomRequest.equipmentIds());
            if (equipmentList.size() != roomRequest.equipmentIds().size()) {
                log.warn("Equipment assignment failed: requested {} items but found {}",
                        roomRequest.equipmentIds().size(), equipmentList.size());
                throw new EntityNotFoundException("One or more equipment not found");
            }
            room.setEquipmentList(equipmentList);
        }
    }

}
