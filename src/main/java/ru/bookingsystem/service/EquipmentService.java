package ru.bookingsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bookingsystem.dto.request.EquipmentRequestDTO;
import ru.bookingsystem.dto.response.EquipmentResponseDTO;
import ru.bookingsystem.entity.Equipment;
import ru.bookingsystem.exception.EntityNotFoundException;
import ru.bookingsystem.mapper.EquipmentMapper;
import ru.bookingsystem.repository.EquipmentRepository;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    @Transactional(readOnly = true)
    public List<EquipmentResponseDTO> getAllEquipments() {
        return equipmentRepository.findAll().stream()
                .map(equipmentMapper::equipmentToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EquipmentResponseDTO getEquipmentById(Long id) {
        return equipmentMapper.equipmentToResponse(equipmentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Equipment with ID: " + id + " not found")));
    }

    @Transactional
    public EquipmentResponseDTO createEquipment(EquipmentRequestDTO equipmentRequest) {
        Equipment newEquipment = equipmentMapper.toEntity(equipmentRequest);
        return equipmentMapper.equipmentToResponse(equipmentRepository.save(newEquipment));
    }

    @Transactional
    public void updateEquipment(EquipmentRequestDTO equipmentRequest, Long id) {
        Equipment existingEquipment = equipmentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Equipment with ID: " + id + " not found"));

        equipmentMapper.updateEntityFromDto(equipmentRequest, existingEquipment);

        equipmentRepository.save(existingEquipment);
    }

    @Transactional
    public void deleteEquipment(Long id) {
        if (equipmentRepository.existsById(id)) {
            equipmentRepository.deleteById(id);
        } else {
            throw new EntityNotFoundException("Equipment with ID: " + id + " not found");
        }
    }

}
