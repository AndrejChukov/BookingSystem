package ru.bookingsystem.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bookingsystem.dto.request.EquipmentRequestDTO;
import ru.bookingsystem.entity.Equipment;
import ru.bookingsystem.exception.EntityNotFoundException;
import ru.bookingsystem.repository.EquipmentRepository;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    @Transactional
    public List<Equipment> getAllEquipments() {
        return equipmentRepository.findAll();
    }

    @Transactional
    public Equipment getEquipmentById(Long id) {
        return equipmentRepository.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Equipment with ID: " + id + " not found"));
    }

    @Transactional
    public Equipment createEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    @Transactional
    public void updateEquipment(EquipmentRequestDTO equipmentRequest) {
        Equipment updatedEquipment = new Equipment();
        updatedEquipment.setId(equipmentRequest.id());
        updatedEquipment.setName(equipmentRequest.name());
        equipmentRepository.findById(equipmentRequest.id())
                .map(e -> {
                    updatedEquipment.setCreatedAt(e.getCreatedAt());
                    updatedEquipment.setUpdatedAt(Instant.now());
                    return equipmentRepository.save(updatedEquipment);
                }).orElseThrow(() -> new EntityNotFoundException("Equipment with ID: " +
                                equipmentRequest.id() + " not found"));
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
