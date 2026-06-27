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
import ru.bookingsystem.dto.request.RoomRequestDTO;
import ru.bookingsystem.dto.response.RoomDetailResponseDTO;
import ru.bookingsystem.dto.response.RoomListResponseDTO;
import ru.bookingsystem.entity.Room;
import ru.bookingsystem.service.RoomService;

import java.util.List;

/**
 * REST controller that exposes room-related API endpoints.
 *
 * <p>This controller manages CRUD operations for room resources. Authentication requirements
 * vary per endpoint; some require ADMIN/WORKER roles, while others are public.
 *
 * <p>Endpoints:
 * - GET    /api/rooms                  : retrieve rooms with optional filtering and sorting (requires ADMIN/WORKER)
 * - GET    /api/rooms/available        : retrieve available rooms (public)
 * - GET    /api/room/{id}              : retrieve room details by id (public)
 * - POST   /api/room                   : create new room (requires ADMIN)
 * - PUT    /api/room/{id}              : update existing room (requires ADMIN/WORKER)
 * - DELETE /api/room/{id}              : delete room by id (requires ADMIN)
 */
@RestController
@RequestMapping("/api")
@AllArgsConstructor
@Slf4j
@Tag(name = "Rooms", description = "Operations for managing room resources")
public class RoomController {

    private final RoomService roomService;

    /**
     * Retrieves rooms with optional filtering by status and sorting.
     *
     * <p>Only accessible to users with ADMIN or WORKER roles. If no status is provided,
     * returns all rooms. Results are sorted by specified field and direction.
     *
     * @param status optional room status filter (AVAILABLE, OCCUPIED, etc.)
     * @param sortBy field to sort by (default: createdAt)
     * @param direction sort direction: "asc" or "desc" (default: desc)
     * @return list of RoomListResponseDTO filtered and sorted as requested
     */
    @GetMapping(value = "/rooms", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    @Operation(summary = "Get all rooms", description = "Retrieve rooms with optional filtering and sorting (requires ADMIN/WORKER)",
            responses = {@ApiResponse(responseCode = "200", description = "List of rooms", content = @Content)})
    public List<RoomListResponseDTO> getAllRooms(
            @Parameter(description = "Room status filter (optional)") @RequestParam(required = false) Room.Status status,
            @Parameter(description = "Sort field (default: createdAt)") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction: asc|desc (default: desc)") @RequestParam(defaultValue = "desc") String direction
    ) {
        log.info("GET /api/rooms status={} sortBy={} direction={}", status, sortBy, direction);
        return roomService.getAllRoomsSorted(status, sortBy, direction);
    }

    /**
     * Retrieves all available rooms.
     *
     * <p>Public endpoint (no authentication required). Returns rooms with AVAILABLE status,
     * sorted by specified field and direction.
     *
     * @param sortBy field to sort by (default: createdAt)
     * @param direction sort direction: "asc" or "desc" (default: desc)
     * @return list of available RoomListResponseDTO objects
     */
    @GetMapping(value = "/rooms/available", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get available rooms", description = "Retrieve rooms with AVAILABLE status (public endpoint)",
            responses = {@ApiResponse(responseCode = "200", description = "List of available rooms", content = @Content)})
    public List<RoomListResponseDTO> getAvailableRooms(
            @Parameter(description = "Sort field (default: createdAt)") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction: asc|desc (default: desc)") @RequestParam(defaultValue = "desc") String direction
    ) {
        log.info("GET /api/rooms/available sortBy={} direction={}", sortBy, direction);
        return roomService.getAllRoomsSorted(Room.Status.AVAILABLE, sortBy, direction);
    }

    /**
     * Retrieves detailed information about a room.
     *
     * <p>Public endpoint (no authentication required). Returns complete room details
     * including associated equipment.
     *
     * @param id room identifier
     * @return RoomDetailResponseDTO with full room information and equipment list
     * @throws ru.bookingsystem.exception.EntityNotFoundException if room with given id not found
     */
    @GetMapping(value = "/room/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get room by id", description = "Retrieve detailed room information (public endpoint)",
            responses = {@ApiResponse(responseCode = "200", description = "Room found", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Room not found")})
    public RoomDetailResponseDTO getRoomById(@Parameter(description = "Room id") @PathVariable("id") Long id) {
        log.info("GET /api/room/{}", id);
        return roomService.getRoomById(id);
    }

    /**
     * Creates a new room.
     *
     * <p>Only accessible to users with ADMIN role. The request must include valid room data
     * and may optionally include equipment ids to associate.
     *
     * @param roomRequest the room data to create
     * @return created RoomDetailResponseDTO with assigned id
     * @throws ru.bookingsystem.exception.EntityNotFoundException if any specified equipment not found
     */
    @PostMapping(value = "/room", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create room", description = "Create a new room (requires ADMIN role)",
            responses = {@ApiResponse(responseCode = "200", description = "Room created", content = @Content)})
    public RoomDetailResponseDTO createRoom(@Valid @RequestBody RoomRequestDTO roomRequest) {
        log.info("POST /api/room request={}", roomRequest);
        return roomService.createRoom(roomRequest);
    }

    /**
     * Updates an existing room.
     *
     * <p>Only accessible to users with ADMIN or WORKER roles. Updates room details and
     * optionally updates associated equipment.
     *
     * @param roomRequest the updated room data
     * @param id room identifier to update
     * @throws ru.bookingsystem.exception.EntityNotFoundException if room or equipment not found
     */
    @PutMapping(value = "/room/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAnyRole('ADMIN', 'WORKER')")
    @Operation(summary = "Update room", description = "Update an existing room (requires ADMIN/WORKER role)",
            responses = {@ApiResponse(responseCode = "200", description = "Room updated", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Room not found")})
    public void updateRoom(@Valid @RequestBody RoomRequestDTO roomRequest, 
                           @Parameter(description = "Room id") @PathVariable("id") Long id) {
        log.info("PUT /api/room/{} request={}", id, roomRequest);
        roomService.updateRoom(roomRequest, id);
    }

    /**
     * Deletes a room by id.
     *
     * <p>Only accessible to users with ADMIN role.
     *
     * @param id room identifier to delete
     * @throws ru.bookingsystem.exception.EntityNotFoundException if room with given id not found
     */
    @DeleteMapping("/room/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete room", description = "Delete room record by identifier (requires ADMIN role)",
            responses = {@ApiResponse(responseCode = "200", description = "Room deleted", content = @Content),
                    @ApiResponse(responseCode = "404", description = "Room not found")})
    public void deleteRoom(@Parameter(description = "Room id") @PathVariable Long id) {
        log.info("DELETE /api/room/{}", id);
        roomService.deleteRoom(id);
    }

}
