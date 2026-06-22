package ru.bookingsystem.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public List<EquipmentResponseDTO> getAllEquipments() {
        return equipmentService.getAllEquipments();
    }

    @GetMapping("/equipment/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public EquipmentResponseDTO getEquipmentById(@PathVariable("id") Long id) {
        return equipmentService.getEquipmentById(id);
    }

    @PostMapping("/equipment")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public EquipmentResponseDTO createEquipment(@Valid @RequestBody EquipmentRequestDTO equipmentRequest) {
        return equipmentService.createEquipment(equipmentRequest);
    }

    @PutMapping("/equipment/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public void updateEquipment(@Valid @RequestBody EquipmentRequestDTO equipmentRequest, @PathVariable("id") Long id) {
        equipmentService.updateEquipment(equipmentRequest, id);
    }

    @DeleteMapping("/equipment/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public void deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
    }

}
