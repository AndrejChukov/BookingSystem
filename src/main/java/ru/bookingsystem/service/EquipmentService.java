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

@Service
@RequiredArgsConstructor
@Slf4j
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    @Transactional(readOnly = true)
    public List<EquipmentResponseDTO> getAllEquipments() {
        log.debug("Fetching all equipments");
        return equipmentRepository.findAll().stream()
                .map(equipmentMapper::equipmentToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EquipmentResponseDTO getEquipmentById(Long id) {
        log.debug("Fetching equipment by id={}", id);
        return equipmentMapper.equipmentToResponse(equipmentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Equipment with ID: " + id + " not found")));
    }

    @Transactional
    public EquipmentResponseDTO createEquipment(EquipmentRequestDTO equipmentRequest) {
        log.info("Creating equipment {}", equipmentRequest);
        Equipment newEquipment = equipmentMapper.toEntity(equipmentRequest);
        Equipment saved = equipmentRepository.save(newEquipment);
        log.debug("Created equipment id={}", saved.getId());
        return equipmentMapper.equipmentToResponse(saved);
    }

    @Transactional
    public void updateEquipment(EquipmentRequestDTO equipmentRequest, Long id) {
        log.info("Updating equipment id={} with {}", id, equipmentRequest);
        Equipment existingEquipment = equipmentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Equipment with ID: " + id + " not found"));

        equipmentMapper.updateEntityFromDto(equipmentRequest, existingEquipment);

        equipmentRepository.save(existingEquipment);
    }

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
