package ru.bookingsystem.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bookingsystem.dto.request.EquipmentRequestDTO;
import ru.bookingsystem.dto.response.EquipmentResponseDTO;
import ru.bookingsystem.entity.Equipment;
import ru.bookingsystem.exception.EntityNotFoundException;
import ru.bookingsystem.mapper.EquipmentMapper;
import ru.bookingsystem.repository.EquipmentRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service that implements equipment-related business logic.
 *
 * <p>This service is responsible for:
 * - retrieving all equipment or individual equipment by id
 * - creating new equipment records
 * - updating existing equipment records
 * - deleting equipment records
 *
 * <p>All operations are delegated to the EquipmentRepository. Equipment DTOs are
 * converted to/from entities using the EquipmentMapper.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    /**
     * Retrieves all equipment records.
     *
     * @return list of EquipmentResponseDTO for all equipment (may be empty)
     */
    @Transactional(readOnly = true)
    public List<EquipmentResponseDTO> getAllEquipments() {
        log.debug("Fetching all equipments");
        return equipmentRepository.findAll().stream()
                .map(equipmentMapper::equipmentToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves equipment by id.
     *
     * @param id equipment identifier
     * @return EquipmentResponseDTO for the given id
     * @throws EntityNotFoundException if equipment with given id does not exist
     */
    @Transactional(readOnly = true)
    public EquipmentResponseDTO getEquipmentById(Long id) {
        log.debug("Fetching equipment by id={}", id);
        return equipmentMapper.equipmentToResponse(equipmentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Equipment with ID: " + id + " not found")));
    }

    /**
     * Creates a new equipment record.
     *
     * <p>Converts the request DTO to an entity, persists it, and returns the created
     * record as a response DTO.
     *
     * @param equipmentRequest the equipment data to create
     * @return EquipmentResponseDTO of the newly created equipment
     */
    @Transactional
    public EquipmentResponseDTO createEquipment(EquipmentRequestDTO equipmentRequest) {
        log.info("Creating equipment {}", equipmentRequest);
        Equipment newEquipment = equipmentMapper.toEntity(equipmentRequest);
        Equipment saved = equipmentRepository.save(newEquipment);
        log.debug("Created equipment id={}", saved.getId());
        return equipmentMapper.equipmentToResponse(saved);
    }

    /**
     * Updates an existing equipment record.
     *
     * <p>Retrieves the equipment by id, updates it with data from the request DTO,
     * and persists the changes.
     *
     * @param equipmentRequest the updated equipment data
     * @param id equipment identifier to update
     * @throws EntityNotFoundException if equipment with given id does not exist
     */
    @Transactional
    public void updateEquipment(EquipmentRequestDTO equipmentRequest, Long id) {
        log.info("Updating equipment id={} with {}", id, equipmentRequest);
        Equipment existingEquipment = equipmentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Equipment with ID: " + id + " not found"));

        equipmentMapper.updateEntityFromDto(equipmentRequest, existingEquipment);

        equipmentRepository.save(existingEquipment);
    }

    /**
     * Deletes an equipment record by id.
     *
     * <p>Verifies that the equipment exists before deletion.
     *
     * @param id equipment identifier to delete
     * @throws EntityNotFoundException if equipment with given id does not exist
     */
    @Transactional
    public void deleteEquipment(Long id) {
        log.info("Deleting equipment id={}", id);
        if (equipmentRepository.existsById(id)) {
            equipmentRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Equipment with ID: " + id + " not found");
        }
    }

}
