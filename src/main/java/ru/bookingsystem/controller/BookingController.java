package ru.bookingsystem.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.dto.request.BookingRequestDTO;
import ru.bookingsystem.dto.response.BookingResponseDTO;
import ru.bookingsystem.service.BookingService;

import java.util.List;

/**
 * REST controller that exposes booking-related API endpoints.
 *
 * <p>Endpoints:
 * - GET  /api/bookings/my    : returns bookings for the currently authenticated user
 * - POST /api/bookings       : creates a new booking for the current user
 * - DELETE /api/bookings/{id}: deletes a booking by id
 *
 * <p>OpenAPI annotations are added so springdoc-openapi can generate Swagger UI documentation.
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Operations for managing bookings")
public class BookingController {

    private final BookingService bookingService;

    /**
     * Returns bookings belonging to the current authenticated user.
     *
     * @return list of BookingResponseDTO for current user
     */
    @GetMapping(value = "/bookings/my", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Get my bookings", description = "Returns bookings for the currently authenticated user",
            responses = {@ApiResponse(responseCode = "200", description = "List of bookings", content = @Content)})
    public List<BookingResponseDTO> getMyBooking() {
        return bookingService.getMyBooking();
    }

    /**
     * Creates a new booking for the current authenticated user.
     *
     * @param request booking request DTO containing room id, start and end time
     * @return created BookingResponseDTO
     */
    @PostMapping(value = "/bookings", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Create booking", description = "Create a new booking for current user",
            responses = {@ApiResponse(responseCode = "200", description = "Created booking", content = @Content)})
    public BookingResponseDTO createBooking(@RequestBody BookingRequestDTO request) {
        return bookingService.createBooking(request);
    }

    /**
     * Deletes a booking by its id.
     *
     * @param id booking id
     */
    @DeleteMapping("/bookings/{id}")
    @Operation(summary = "Delete booking", description = "Deletes booking with the given id",
            responses = {@ApiResponse(responseCode = "200", description = "Booking deleted", content = @Content)})
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }

}
