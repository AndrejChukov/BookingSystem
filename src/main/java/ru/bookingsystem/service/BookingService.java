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

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
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

    /**
     * Create new Booking.
     *
     * <p>This method validates the booking request based on time (8:00-21:00 UTC+3)
     * and date (must be same day) constraints.
     *
     * @param request the booking request DTO
     * @return the created booking response DTO
     * @throws BadRequestParametersException if date or time aren't valid,
     * or if the room is already booked for the given time range
     */

    @Transactional
    public BookingResponseDTO createBooking(BookingRequestDTO request) {
        Instant startTime = request.startTime();
        Instant endTime = request.endTime();

        LocalTime startOffice = LocalTime.of(8, 0);
        LocalTime endOffice = LocalTime.of(21, 0);
        ZoneId localZone = ZoneId.systemDefault();

        LocalTime bookingStartLocal = startTime.atZone(localZone).toLocalTime();
        LocalTime bookingEndLocal = endTime.atZone(localZone).toLocalTime();

        LocalDate bookingStartDate = startTime.atZone(localZone).toLocalDate();
        LocalDate bookingEndDate = endTime.atZone(localZone).toLocalDate();

        if (!bookingStartDate.equals(bookingEndDate)) {
            throw new BadRequestParametersException("Booking must start and end on the same day");
        }

        if (bookingStartLocal.isBefore(startOffice) || bookingStartLocal.isAfter(endOffice)) {
            throw new BadRequestParametersException("Booking start time must be within working hours (08:00 - 21:00)");
        }

        if (bookingEndLocal.isAfter(endOffice) || bookingEndLocal.isBefore(startOffice)) {
            throw new BadRequestParametersException("Booking end time must be within working hours (08:00 - 21:00)");
        }

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

        User currentUser = getCurrentUser();

        if (currentUser.getCountReservation() >= 3) {
            throw new BadRequestParametersException("You have reached the maximum number of active bookings (3)");
        }

        Booking booking = bookingMapper.toEntity(request);
        booking.setRoom(room);

        booking.setUser(currentUser);
        currentUser.setCountReservation(currentUser.getCountReservation() + 1);

        booking.setBookingStatus(Booking.BookingStatus.CONFIRMED);

        return bookingMapper.toResponse(bookingRepository.save(booking));
    }

    /**
     * Allow cancel booking if user possesses it. Otherwise, throws exception.
     *
     * @param id
     * @throws BadRequestParametersException if user does not possess the booking
     * @throws EntityNotFoundException if booking with given id does not exist
     */

    @Transactional
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Booking with ID: " + id + " not found"));
        User currentUser = getCurrentUser();
        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestParametersException("You are not authorized to cancel this booking");
        }

        // Mark booking cancelled and decrement reservation count for the owner
        booking.setBookingStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        currentUser.setCountReservation(currentUser.getCountReservation() - 1);
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
        // Only decrement reservation count if the booking exists and belongs to the current user.
        Booking booking = bookingRepository.findById(id).orElse(null);
        if (booking == null) {
            // Nothing to delete
            return;
        }

        User currentUser = getCurrentUser();
        if (!booking.getUser().getId().equals(currentUser.getId())) {
            throw new BadRequestParametersException("You are not authorized to delete this booking");
        }

        bookingRepository.deleteById(id);
        currentUser.setCountReservation(currentUser.getCountReservation() - 1);
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
