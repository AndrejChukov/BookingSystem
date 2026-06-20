package ru.bookingsystem.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.dto.request.EquipmentRequestDTO;
import ru.bookingsystem.dto.response.EquipmentResponseDTO;
import ru.bookingsystem.service.EquipmentService;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping("/equipments")
    public List<EquipmentResponseDTO> getAllEquipments() {
        return equipmentService.getAllEquipments();
    }

    @GetMapping("/equipment/{id}")
    public EquipmentResponseDTO getEquipmentById(@PathVariable("id") Long id) {
        return equipmentService.getEquipmentById(id);
    }

    @PostMapping("/equipment")
    public EquipmentResponseDTO createEquipment(@Valid @RequestBody EquipmentRequestDTO equipmentRequest) {
        return equipmentService.createEquipment(equipmentRequest);
    }

    @PutMapping("/equipment/{id}")
    public void updateEquipment(@Valid @RequestBody EquipmentRequestDTO equipmentRequest, @PathVariable("id") Long id) {
        equipmentService.updateEquipment(equipmentRequest, id);
    }

    @DeleteMapping("/equipment/{id}")
    public void deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
    }

}
