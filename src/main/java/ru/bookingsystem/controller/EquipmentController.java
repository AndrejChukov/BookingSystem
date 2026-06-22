package ru.bookingsystem.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.dto.request.EquipmentRequestDTO;
import ru.bookingsystem.dto.response.EquipmentResponseDTO;
import ru.bookingsystem.service.EquipmentService;

import java.util.List;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
public class EquipmentController {

    private final EquipmentService equipmentService;

    @GetMapping("/equipments")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public List<EquipmentResponseDTO> getAllEquipments() {
        log.info("GET /api/equipments");
        return equipmentService.getAllEquipments();
    }

    @GetMapping("/equipment/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public EquipmentResponseDTO getEquipmentById(@PathVariable("id") Long id) {
        log.info("GET /api/equipment/{}", id);
        return equipmentService.getEquipmentById(id);
    }

    @PostMapping("/equipment")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public EquipmentResponseDTO createEquipment(@Valid @RequestBody EquipmentRequestDTO equipmentRequest) {
        log.info("POST /api/equipment request={}", equipmentRequest);
        return equipmentService.createEquipment(equipmentRequest);
    }

    @PutMapping("/equipment/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public void updateEquipment(@Valid @RequestBody EquipmentRequestDTO equipmentRequest, @PathVariable("id") Long id) {
        log.info("PUT /api/equipment/{} request={}", id, equipmentRequest);
        equipmentService.updateEquipment(equipmentRequest, id);
    }

    @DeleteMapping("/equipment/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    public void deleteEquipment(@PathVariable Long id) {
        log.info("DELETE /api/equipment/{}", id);
        equipmentService.deleteEquipment(id);
    }

}
