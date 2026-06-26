package ru.bookingsystem.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.transaction.annotation.Transactional;
import ru.bookingsystem.dto.request.BookingRequestDTO;
import ru.bookingsystem.dto.response.BookingResponseDTO;
import ru.bookingsystem.entity.Booking;
import ru.bookingsystem.entity.Room;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.exception.BadRequestParametersException;
import ru.bookingsystem.mapper.BookingMapper;
import ru.bookingsystem.repository.BookingRepository;
import ru.bookingsystem.repository.RoomRepository;
import ru.bookingsystem.repository.UserRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BookingServiceIT {

    @Autowired private BookingService bookingService;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private UserRepository userRepository;

    private Room testRoom;
    private User testUser;

    @BeforeEach
    void setUp() {
        testRoom = new Room();
        testRoom.setName("Test Room");
        testRoom.setCapacity(10);
        testRoom.setStatus(Room.Status.AVAILABLE);
        roomRepository.save(testRoom);

        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("testuser@example.com");
        testUser.setPassword("password");
        testUser.setRole(User.Role.USER);
        userRepository.save(testUser);

        setupSecurityContext(testUser);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void createBooking_ShouldSuccess_WhenRoomIsAvailable() {
        Instant startTime = Instant.now().plus(1, ChronoUnit.HOURS);
        Instant endTime = startTime.plus(2, ChronoUnit.HOURS);

        BookingResponseDTO response = bookingService
                .createBooking(new BookingRequestDTO(testRoom.getId(), startTime, endTime));

        assertNotNull(response);
        assertEquals(Booking.BookingStatus.CONFIRMED, response.bookingStatus());
        assertEquals(startTime, response.startTime());

        List<Booking> saved = bookingRepository.findAllByUserId(testUser.getId());
        assertEquals(1, saved.size());
        assertEquals(testRoom.getId(), saved.get(0).getRoom().getId());
    }

    @Test
    public void createBooking_ShouldThrowBadRequest_WhenRoomIsAlreadyBooked() {
        Instant startTime = Instant.now().plus(1, ChronoUnit.HOURS);
        Instant endTime = startTime.plus(2, ChronoUnit.HOURS);

        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), startTime, endTime));

        assertThrows(BadRequestParametersException.class,
                () -> bookingService.createBooking(
                        new BookingRequestDTO(testRoom.getId(),
                                startTime.plus(30, ChronoUnit.MINUTES),
                                endTime.plus(30, ChronoUnit.MINUTES))));
    }

    public void setupSecurityContext(User user) {
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "none")
                .claim("id", user.getId())
                .claim("sub", user.getUsername())
                .build();

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(jwt, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    public void getMyBooking_ShouldReturnBookings_WhenUserHasBookings() {
        Instant startTime1 = Instant.now().plus(1, ChronoUnit.HOURS);
        Instant endTime1 = startTime1.plus(2, ChronoUnit.HOURS);
        Instant startTime2 = Instant.now().plus(5, ChronoUnit.HOURS);
        Instant endTime2 = startTime2.plus(2, ChronoUnit.HOURS);

        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), startTime1, endTime1));
        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), startTime2, endTime2));

        List<BookingResponseDTO> bookings = bookingService.getMyBooking();

        assertNotNull(bookings);
        assertEquals(2, bookings.size());
        assertTrue(bookings.stream().allMatch(b -> b.bookingStatus() == Booking.BookingStatus.CONFIRMED));
    }

    @Test
    public void getMyBooking_ShouldReturnEmpty_WhenUserHasNoBookings() {
        List<BookingResponseDTO> bookings = bookingService.getMyBooking();

        assertNotNull(bookings);
        assertTrue(bookings.isEmpty());
    }

    @Test
    public void getMyBooking_ShouldReturnOnlyCurrentUserBookings() {
        User anotherUser = new User();
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword("password");
        anotherUser.setRole(User.Role.USER);
        userRepository.save(anotherUser);

        Instant startTime1 = Instant.now().plus(1, ChronoUnit.HOURS);
        Instant endTime1 = startTime1.plus(2, ChronoUnit.HOURS);
        Instant startTime2 = Instant.now().plus(5, ChronoUnit.HOURS);
        Instant endTime2 = startTime2.plus(2, ChronoUnit.HOURS);

        // Create booking for current user
        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), startTime1, endTime1));

        // Create booking for another user
        setupSecurityContext(anotherUser);
        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), startTime2, endTime2));

        // Switch back to test user
        setupSecurityContext(testUser);

        List<BookingResponseDTO> bookings = bookingService.getMyBooking();

        assertNotNull(bookings);
        assertEquals(1, bookings.size());
        assertEquals(startTime1, bookings.get(0).startTime());
    }

    @Test
    public void deleteBooking_ShouldDeleteBooking_WhenIdIsValid() {
        Instant startTime = Instant.now().plus(1, ChronoUnit.HOURS);
        Instant endTime = startTime.plus(2, ChronoUnit.HOURS);

        BookingResponseDTO createdBooking = bookingService
                .createBooking(new BookingRequestDTO(testRoom.getId(), startTime, endTime));

        List<Booking> beforeDelete = bookingRepository.findAllByUserId(testUser.getId());
        assertEquals(1, beforeDelete.size());

        bookingService.deleteBooking(beforeDelete.get(0).getId());

        List<Booking> afterDelete = bookingRepository.findAllByUserId(testUser.getId());
        assertTrue(afterDelete.isEmpty());
    }

    @Test
    public void deleteBooking_ShouldNotThrowException_WhenIdDoesNotExist() {
        assertDoesNotThrow(() -> bookingService.deleteBooking(999L));
    }

    @Test
    public void deleteBooking_ShouldDeleteMultipleBookings_Sequentially() {
        Instant startTime1 = Instant.now().plus(1, ChronoUnit.HOURS);
        Instant endTime1 = startTime1.plus(2, ChronoUnit.HOURS);
        Instant startTime2 = Instant.now().plus(5, ChronoUnit.HOURS);
        Instant endTime2 = startTime2.plus(2, ChronoUnit.HOURS);

        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), startTime1, endTime1));
        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), startTime2, endTime2));

        List<Booking> allBookings = bookingRepository.findAllByUserId(testUser.getId());
        assertEquals(2, allBookings.size());

        bookingService.deleteBooking(allBookings.get(0).getId());

        List<Booking> afterFirstDelete = bookingRepository.findAllByUserId(testUser.getId());
        assertEquals(1, afterFirstDelete.size());

        bookingService.deleteBooking(allBookings.get(1).getId());

        // Assert
        List<Booking> afterSecondDelete = bookingRepository.findAllByUserId(testUser.getId());
        assertTrue(afterSecondDelete.isEmpty());
    }

}
