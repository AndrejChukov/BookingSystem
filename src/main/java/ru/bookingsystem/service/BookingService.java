package ru.bookingsystem.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.bookingsystem.dto.request.BookingRequestDTO;
import ru.bookingsystem.dto.response.BookingResponseDTO;
import ru.bookingsystem.entity.Booking;
import ru.bookingsystem.entity.Room;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.exception.BadRequestParametersException;
import ru.bookingsystem.exception.EntityNotFoundException;
import ru.bookingsystem.mapper.BookingMapper;
import ru.bookingsystem.repository.BookingRepository;
import ru.bookingsystem.repository.RoomRepository;
import ru.bookingsystem.repository.UserRepository;

import java.awt.print.Book;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service that implements booking-related business logic.
 *
 * <p>This service is responsible for:
 * - returning bookings for the currently authenticated user
 * - creating new bookings (with validation and conflict checks)
 * - deleting bookings
 *
 * <p>It depends on JPA repositories and a MapStruct mapper to convert between
 * entities and DTOs.
 */
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private  final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    /**
     * Returns list of bookings that belong to the currently authenticated user.
     *
     * <p>The currently authenticated user is resolved from the security context
     * (see {@link #getCurrentUser()}). The result is mapped to {@link BookingResponseDTO}
     * using the configured {@code BookingMapper}.
     *
     * @return list of booking response DTOs for the current user (may be empty)
     * @throws EntityNotFoundException when the current user cannot be resolved
     */
    @Transactional(readOnly = true)
    public List<BookingResponseDTO> getMyBooking() {
        return bookingRepository.findAllByUserId(getCurrentUser().getId()).stream()
                .map(bookingMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        Instant startTime = request.startTime();
        Instant endTime = request.endTime();
        if (startTime.compareTo(endTime) >= 0) {
            throw new BadRequestParametersException("Start time must be before end time");
        }
        if (startTime.compareTo(Instant.now()) < 0) {
            throw new BadRequestParametersException("Start time must be in the future");
        }

        Room room = roomRepository.findByIdWithLock(request.roomId())
                .orElseThrow(() -> new EntityNotFoundException("Room with ID " + request.roomId() + " not found"));

        boolean overlapped = bookingRepository.existsOverlappingBookings(room.getId(), startTime, endTime);
        if (overlapped) {
            throw new BadRequestParametersException("Current room is already booked for the given time range");
        }

        Booking booking = bookingMapper.toEntity(request);
        booking.setRoom(room);
        booking.setUser(getCurrentUser());
        booking.setBookingStatus(Booking.BookingStatus.CONFIRMED);

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    /**
     * Deletes a booking by id.
     *
     * <p>This delegates to the repository deleteById method. If the id does not exist,
     * JPA will not throw an exception;
     *
     * @param id booking id to delete
     */
    @Transactional
    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new EntityNotFoundException("User not authenticated");
        }
        Jwt jwt = (Jwt) auth.getPrincipal();
        Long userId = jwt.getClaim("id");
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + userId + " not found"));
    }

}
