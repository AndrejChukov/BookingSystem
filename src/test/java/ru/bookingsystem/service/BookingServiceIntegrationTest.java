package ru.bookingsystem.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import ru.bookingsystem.dto.request.BookingRequestDTO;
import ru.bookingsystem.dto.response.BookingResponseDTO;
import ru.bookingsystem.entity.Booking;
import ru.bookingsystem.entity.Room;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.exception.BadRequestParametersException;
import ru.bookingsystem.exception.EntityNotFoundException;
import ru.bookingsystem.repository.BookingRepository;
import ru.bookingsystem.repository.RoomRepository;
import ru.bookingsystem.repository.UserRepository;
import ru.bookingsystem.support.AbstractIntegrationTest;
import ru.bookingsystem.support.BookingTestTimes;
import ru.bookingsystem.support.SecurityTestHelper;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
class BookingServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired private BookingService bookingService;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private RoomRepository roomRepository;
    @Autowired private UserRepository userRepository;

    private Room testRoom;
    private User testUser;
    private User adminUser;

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

        adminUser = new User();
        adminUser.setUsername("adminuser");
        adminUser.setEmail("adminuser@example.com");
        adminUser.setPassword("password");
        adminUser.setRole(User.Role.ADMIN);
        userRepository.save(adminUser);

        SecurityTestHelper.authenticateAs(testUser);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void createBooking_shouldSucceed_whenRoomIsAvailable() {
        Instant startTime = BookingTestTimes.workingStart();
        Instant endTime = BookingTestTimes.workingEnd(startTime);

        BookingResponseDTO response = bookingService
                .createBooking(new BookingRequestDTO(testRoom.getId(), startTime, endTime));

        assertNotNull(response);
        assertNotNull(response.id());
        assertEquals(Booking.BookingStatus.CONFIRMED, response.bookingStatus());
        assertEquals(startTime, response.startTime());

        List<Booking> saved = bookingRepository.findAllByUserId(testUser.getId());
        assertEquals(1, saved.size());
        assertEquals(testRoom.getId(), saved.get(0).getRoom().getId());
    }

    @Test
    void createBooking_shouldThrowBadRequest_whenRoomIsAlreadyBooked() {
        Instant startTime = BookingTestTimes.workingStart();
        Instant endTime = BookingTestTimes.workingEnd(startTime);

        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), startTime, endTime));

        assertThrows(BadRequestParametersException.class,
                () -> bookingService.createBooking(
                        new BookingRequestDTO(testRoom.getId(),
                                startTime.plus(30, ChronoUnit.MINUTES),
                                endTime.plus(30, ChronoUnit.MINUTES))));
    }

    @Test
    void createBooking_shouldThrowBadRequest_whenMaxActiveBookingsReached() {
        Instant firstStart = BookingTestTimes.workingStart();
        Instant firstEnd = BookingTestTimes.workingEnd(firstStart);
        Instant secondStart = BookingTestTimes.laterWorkingStart(firstStart);
        Instant secondEnd = BookingTestTimes.workingEnd(secondStart);
        Instant thirdStart = BookingTestTimes.laterWorkingStart(secondStart);
        Instant thirdEnd = BookingTestTimes.workingEnd(thirdStart);
        Instant fourthStart = BookingTestTimes.laterWorkingStart(thirdStart);
        Instant fourthEnd = BookingTestTimes.workingEnd(fourthStart);

        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), firstStart, firstEnd));
        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), secondStart, secondEnd));
        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), thirdStart, thirdEnd));

        assertThrows(BadRequestParametersException.class,
                () -> bookingService.createBooking(
                        new BookingRequestDTO(testRoom.getId(), fourthStart, fourthEnd)));
    }

    @Test
    void getMyBooking_shouldReturnBookings_whenUserHasBookings() {
        Instant startTime1 = BookingTestTimes.workingStart();
        Instant endTime1 = BookingTestTimes.workingEnd(startTime1);
        Instant startTime2 = BookingTestTimes.laterWorkingStart(startTime1);
        Instant endTime2 = BookingTestTimes.workingEnd(startTime2);

        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), startTime1, endTime1));
        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), startTime2, endTime2));

        List<BookingResponseDTO> bookings = bookingService.getMyBooking();

        assertEquals(2, bookings.size());
        assertTrue(bookings.stream().allMatch(b -> b.bookingStatus() == Booking.BookingStatus.CONFIRMED));
    }

    @Test
    void getMyBooking_shouldReturnEmpty_whenUserHasNoBookings() {
        assertTrue(bookingService.getMyBooking().isEmpty());
    }

    @Test
    void getMyBooking_shouldReturnOnlyCurrentUserBookings() {
        User anotherUser = new User();
        anotherUser.setUsername("anotheruser");
        anotherUser.setEmail("another@example.com");
        anotherUser.setPassword("password");
        anotherUser.setRole(User.Role.USER);
        userRepository.save(anotherUser);

        Instant startTime1 = BookingTestTimes.workingStart();
        Instant endTime1 = BookingTestTimes.workingEnd(startTime1);
        Instant startTime2 = BookingTestTimes.laterWorkingStart(startTime1);
        Instant endTime2 = BookingTestTimes.workingEnd(startTime2);

        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), startTime1, endTime1));

        SecurityTestHelper.authenticateAs(anotherUser);
        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), startTime2, endTime2));

        SecurityTestHelper.authenticateAs(testUser);

        List<BookingResponseDTO> bookings = bookingService.getMyBooking();

        assertEquals(1, bookings.size());
        assertEquals(startTime1, bookings.get(0).startTime());
    }

    @Test
    void deleteBooking_shouldDeleteBooking_whenCallerIsAdmin() {
        Instant startTime = BookingTestTimes.workingStart();
        Instant endTime = BookingTestTimes.workingEnd(startTime);

        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), startTime, endTime));

        Long bookingId = bookingRepository.findAllByUserId(testUser.getId()).get(0).getId();

        SecurityTestHelper.authenticateAs(adminUser);
        bookingService.deleteBooking(bookingId);

        assertTrue(bookingRepository.findById(bookingId).isEmpty());
    }

    @Test
    void deleteBooking_shouldThrowEntityNotFound_whenIdDoesNotExist() {
        SecurityTestHelper.authenticateAs(adminUser);
        assertThrows(EntityNotFoundException.class, () -> bookingService.deleteBooking(999L));
    }

    @Test
    void cancelBooking_shouldCancelConfirmedBooking() {
        Instant startTime = BookingTestTimes.workingStart();
        Instant endTime = BookingTestTimes.workingEnd(startTime);

        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), startTime, endTime));

        Long id = bookingRepository.findAllByUserId(testUser.getId()).get(0).getId();

        bookingService.cancelBooking(id);

        Booking cancelled = bookingRepository.findById(id).orElseThrow();
        assertEquals(Booking.BookingStatus.CANCELLED, cancelled.getBookingStatus());
    }

    @Test
    void cancelBooking_shouldBeIdempotent_whenAlreadyCancelled() {
        Instant startTime = BookingTestTimes.workingStart();
        Instant endTime = BookingTestTimes.workingEnd(startTime);

        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), startTime, endTime));
        Long id = bookingRepository.findAllByUserId(testUser.getId()).get(0).getId();

        bookingService.cancelBooking(id);
        bookingService.cancelBooking(id);

        assertEquals(Booking.BookingStatus.CANCELLED,
                bookingRepository.findById(id).orElseThrow().getBookingStatus());
    }

    @Test
    void cancelBooking_shouldAllowNewBooking_afterCancellation() {
        Instant startTime = BookingTestTimes.workingStart();
        Instant endTime = BookingTestTimes.workingEnd(startTime);

        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), startTime, endTime));
        Long id = bookingRepository.findAllByUserId(testUser.getId()).get(0).getId();
        bookingService.cancelBooking(id);

        Instant newStart = BookingTestTimes.laterWorkingStart(startTime);
        Instant newEnd = BookingTestTimes.workingEnd(newStart);

        assertDoesNotThrow(() -> bookingService.createBooking(
                new BookingRequestDTO(testRoom.getId(), newStart, newEnd)));
    }

    @Test
    void cancelBooking_shouldThrow_whenNotOwner() {
        User anotherUser = new User();
        anotherUser.setUsername("anotheruser2");
        anotherUser.setEmail("another2@example.com");
        anotherUser.setPassword("password");
        anotherUser.setRole(User.Role.USER);
        userRepository.save(anotherUser);

        SecurityTestHelper.authenticateAs(anotherUser);
        Instant startTime = BookingTestTimes.workingStart();
        Instant endTime = BookingTestTimes.workingEnd(startTime);
        bookingService.createBooking(new BookingRequestDTO(testRoom.getId(), startTime, endTime));

        Long otherId = bookingRepository.findAllByUserId(anotherUser.getId()).get(0).getId();

        SecurityTestHelper.authenticateAs(testUser);

        assertThrows(BadRequestParametersException.class, () -> bookingService.cancelBooking(otherId));
    }
}
