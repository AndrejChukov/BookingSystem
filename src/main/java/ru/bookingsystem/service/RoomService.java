package ru.bookingsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

/**
 * Service that implements room-related business logic.
 *
 * <p>This service is responsible for:
 * - retrieving rooms with optional filtering and sorting
 * - retrieving individual room details by id
 * - creating new room records with associated equipment
 * - updating existing room records and their equipment associations
 * - deleting room records
 *
 * <p>Room operations interact with both RoomRepository and EquipmentRepository.
 * DTOs are converted to/from entities using RoomMapper.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RoomService {

    private final RoomRepository roomRepository;
    private final EquipmentRepository equipmentRepository;
    private final RoomMapper roomMapper;

    /**
     * Retrieves rooms with optional status filtering and sorting.
     *
     * <p>If status is null, returns all rooms. Results are sorted by the specified field
     * in the specified direction (asc/desc).
     *
     * @param status optional room status filter (null returns all rooms)
     * @param sortedBy field name to sort by
     * @param direction sort direction ("asc" or "desc")
     * @return list of RoomListResponseDTO filtered and sorted as requested
     */
    @Transactional(readOnly = true)
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

    /**
     * Retrieves detailed information for a room including associated equipment.
     *
     * @param id room identifier
     * @return RoomDetailResponseDTO with full room information
     * @throws EntityNotFoundException if room with given id does not exist
     */
    @Transactional(readOnly = true)
    public RoomDetailResponseDTO getRoomById(Long id) {
        log.debug("Fetching room by id={}", id);
        return roomMapper.toRoomDetailResponseDTO(roomRepository.findById(id)
                .orElseThrow(() ->
                new EntityNotFoundException("Room with ID: " + id + " not found")));
    }

    /**
     * Creates a new room record.
     *
     * <p>Converts the request DTO to an entity, assigns equipment if provided,
     * persists it, and returns the created record as a response DTO.
     *
     * @param roomRequest the room data to create (includes optional equipment ids)
     * @return RoomDetailResponseDTO of the newly created room
     * @throws EntityNotFoundException if any specified equipment not found
     */
    @Transactional
    public RoomDetailResponseDTO createRoom(RoomRequestDTO roomRequest) {
        log.info("Creating room: {}", roomRequest);
        Room newRoom = roomMapper.toEntity(roomRequest);
        assignEquipment(roomRequest, newRoom);
        Room saved = roomRepository.save(newRoom);
        log.debug("Created room id={}", saved.getId());
        return roomMapper.toRoomDetailResponseDTO(saved);
    }

    /**
     * Updates an existing room record and its equipment associations.
     *
     * <p>Retrieves the room by id, updates its properties from the request DTO,
     * updates equipment associations, and persists the changes.
     *
     * @param roomRequest the updated room data (includes optional equipment ids)
     * @param id room identifier to update
     * @throws EntityNotFoundException if room or equipment not found
     */
    @Transactional
    public void updateRoom(RoomRequestDTO roomRequest, Long id) {
        log.info("Updating room id={} with {}", id, roomRequest);
        Room existingRoom = roomRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Room with ID: " + id + " not found"));

        roomMapper.updateEntityFromDto(roomRequest, existingRoom);

        assignEquipment(roomRequest, existingRoom);

        roomRepository.save(existingRoom);
    }

    /**
     * Deletes a room record by id.
     *
     * <p>Verifies that the room exists before deletion.
     *
     * @param id room identifier to delete
     * @throws EntityNotFoundException if room with given id does not exist
     */
    @Transactional
    public void deleteRoom(Long id) {
        log.info("Deleting room id={}", id);
        if (roomRepository.existsById(id)) {
            roomRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Room with ID: " + id + " not found");
        }
    }

    /**
     * Helper method to assign equipment to a room.
     *
     * <p>Fetches equipment by the provided ids and assigns them to the room.
     * Throws exception if any equipment id does not exist.
     *
     * @param roomRequest room request containing equipment ids to assign
     * @param room the room entity to assign equipment to
     * @throws EntityNotFoundException if any specified equipment not found
     */
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
