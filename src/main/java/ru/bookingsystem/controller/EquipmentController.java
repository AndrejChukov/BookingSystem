package ru.bookingsystem.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.entity.Equipment;
import ru.bookingsystem.service.EquipmentService;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping("/equipments")
    public List<Equipment> getAllEquipments() {
        return equipmentService.getAllEquipments();
    }

    @PostMapping("/equipment")
    public Equipment createEquipment(@RequestBody Equipment equipment) {
        return equipmentService.createEquipment(equipment);
    }

}
