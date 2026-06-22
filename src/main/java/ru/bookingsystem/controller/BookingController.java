package ru.bookingsystem.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.bookingsystem.dto.request.BookingRequestDTO;
import ru.bookingsystem.dto.response.BookingResponseDTO;
import ru.bookingsystem.entity.Booking;
import ru.bookingsystem.mapper.BookingMapper;
import ru.bookingsystem.service.BookingService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/bookings/my")
    public List<BookingResponseDTO> getMyBooking() {
        return bookingService.getMyBooking();
    }

    @PostMapping("/bookings")
    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        return bookingService.createBooking(request);
    }

    @DeleteMapping("/bookings/{id}")
    public void deleteBooking(@PathVariable Long id) {
        bookingService.deleteBooking(id);
    }

}
