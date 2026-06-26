package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.dto.request.EquipmentRequestDTO;
import ru.bookingsystem.dto.response.EquipmentResponseDTO;
import ru.bookingsystem.service.EquipmentService;

import java.util.List;

/**
 * REST controller that exposes equipment-related API endpoints.
 *
 * <p>This controller manages CRUD operations for equipment resources. All endpoints
 * require ADMIN or WORKER role authentication, except where explicitly noted.
 *
 * <p>Endpoints:
 * - GET    /api/equipments      : retrieve all equipment
 * - GET    /api/equipment/{id}  : retrieve equipment by id
 * - POST   /api/equipment       : create new equipment
 * - PUT    /api/equipment/{id}  : update existing equipment
 * - DELETE /api/equipment/{id}  : delete equipment by id
 */
@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
@Tag(name = "Equipment", description = "Operations for managing equipment resources")
public class EquipmentController {

    private final EquipmentService equipmentService;

    /**
     * Retrieves all equipment.
     *
     * <p>Only accessible to users with ADMIN or WORKER roles.
     *
     * @return list of all EquipmentResponseDTO objects
     */
    @GetMapping(value = "/equipments", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    @Operation(summary = "Get all equipment", description = "Retrieve all equipment records",
            responses = {@ApiResponse(responseCode = "200", description = "List of equipment", content = @Content)})
    public List<EquipmentResponseDTO> getAllEquipments() {
        log.info("GET /api/equipments");
        return equipmentService.getAllEquipments();
    }

    /**
     * Retrieves equipment by id.
     *
     * <p>Only accessible to users with ADMIN or WORKER roles.
     *
     * @param id equipment identifier
     * @return EquipmentResponseDTO for the given id
     * @throws ru.bookingsystem.exception.EntityNotFoundException if equipment with given id not found
     */
    @GetMapping(value = "/equipment/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    @Operation(summary = "Get equipment by id", description = "Retrieve equipment record by identifier",
            responses = {@ApiResponse(responseCode = "200", description = "Equipment found", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Equipment not found")})
    public EquipmentResponseDTO getEquipmentById(
            @Parameter(description = "Equipment id") @PathVariable("id") Long id) {
        log.info("GET /api/equipment/{}", id);
        return equipmentService.getEquipmentById(id);
    }

    /**
     * Creates a new equipment record.
     *
     * <p>Only accessible to users with ADMIN or WORKER roles. The request body must
     * be a valid EquipmentRequestDTO.
     *
     * @param equipmentRequest the equipment data to create
     * @return created EquipmentResponseDTO with assigned id
     */
    @PostMapping(value = "/equipment", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    @Operation(summary = "Create equipment", description = "Create a new equipment record",
            responses = {@ApiResponse(responseCode = "200", description = "Equipment created", content = @Content)})
    public EquipmentResponseDTO createEquipment(@Valid @RequestBody EquipmentRequestDTO equipmentRequest) {
        log.info("POST /api/equipment request={}", equipmentRequest);
        return equipmentService.createEquipment(equipmentRequest);
    }

    /**
     * Updates an existing equipment record.
     *
     * <p>Only accessible to users with ADMIN or WORKER roles. Replaces all mutable fields
     * of the equipment with given id.
     *
     * @param equipmentRequest the updated equipment data
     * @param id equipment identifier to update
     * @throws ru.bookingsystem.exception.EntityNotFoundException if equipment with given id not found
     */
    @PutMapping(value = "/equipment/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    @Operation(summary = "Update equipment", description = "Update an existing equipment record",
            responses = {@ApiResponse(responseCode = "200", description = "Equipment updated", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Equipment not found")})
    public void updateEquipment(@Valid @RequestBody EquipmentRequestDTO equipmentRequest,
                                @Parameter(description = "Equipment id") @PathVariable("id") Long id) {
        log.info("PUT /api/equipment/{} request={}", id, equipmentRequest);
        equipmentService.updateEquipment(equipmentRequest, id);
    }

    /**
     * Deletes equipment by id.
     *
     * <p>Only accessible to users with ADMIN or WORKER roles.
     *
     * @param id equipment identifier to delete
     * @throws ru.bookingsystem.exception.EntityNotFoundException if equipment with given id not found
     */
    @DeleteMapping("/equipment/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    @Operation(summary = "Delete equipment", description = "Delete equipment record by identifier",
            responses = {@ApiResponse(responseCode = "200", description = "Equipment deleted", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Equipment not found")})
    public void deleteEquipment(@Parameter(description = "Equipment id") @PathVariable Long id) {
        log.info("DELETE /api/equipment/{}", id);
        equipmentService.deleteEquipment(id);
    }

}
