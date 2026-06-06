package ru.bookingsystem.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.bookingsystem.entity.Equipment;
import ru.bookingsystem.repository.EquipmentRepository;

import java.util.List;

@Service
@AllArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;

    public List<Equipment> getAllEquipments() {
        return equipmentRepository.findAll();
    }

    public Equipment createEquipment(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

}
