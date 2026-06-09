package ru.bookingsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bookingsystem.dto.request.EquipmentRequestDTO;
import ru.bookingsystem.entity.Equipment;
import ru.bookingsystem.exception.EntityNotFoundException;
import ru.bookingsystem.mapper.EquipmentMapper;
import ru.bookingsystem.repository.EquipmentRepository;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;

    @Transactional(readOnly = true)
    public List<Equipment> getAllEquipments() {
        return equipmentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Equipment getEquipmentById(Long id) {
        return equipmentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Equipment with ID: " + id + " not found"));
    }

    @Transactional
    public Equipment createEquipment(EquipmentRequestDTO equipmentRequest) {
        Equipment newEquipment = equipmentMapper.toEntity(equipmentRequest);
        return equipmentRepository.save(newEquipment);
    }

    @Transactional
    public void updateEquipment(EquipmentRequestDTO equipmentRequest, Long id) {
        Equipment existingEquipment = equipmentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Equipment with ID: " + id + " not found"));

        equipmentMapper.updateEntityFromDto(equipmentRequest, existingEquipment);
        existingEquipment.setUpdatedAt(Instant.now());

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
