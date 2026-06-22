package ru.bookingsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import ru.bookingsystem.dto.request.BookingRequestDTO;
import ru.bookingsystem.dto.response.BookingResponseDTO;
import ru.bookingsystem.entity.Booking;
import ru.bookingsystem.exception.BadRequestParametersException;
import ru.bookingsystem.exception.EntityNotFoundException;
import ru.bookingsystem.mapper.BookingMapper;
import ru.bookingsystem.repository.BookingRepository;

import java.awt.print.Book;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    public List<BookingResponseDTO> getMyBooking() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new EntityNotFoundException("User not authenticated");
        }
        Jwt jwt = (Jwt) auth.getPrincipal();
        Long userId = jwt.getClaim("id");

        return bookingRepository.findAllByUserId(userId).stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        if (request.startTime().compareTo(request.endTime()) >= 0) {
            throw new BadRequestParametersException("Start time must be before end time");
        }
        if (request.startTime().compareTo(Instant.now()) < 0) {
            throw new BadRequestParametersException("Start time must be in the future");
        }
        // TODO: add Locking and check for overlapping bookings
        Booking booking = bookingMapper.toEntity(request);
        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

}
