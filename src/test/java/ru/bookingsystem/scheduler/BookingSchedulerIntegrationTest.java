package ru.bookingsystem.scheduler;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.bookingsystem.entity.Booking;
import ru.bookingsystem.entity.Room;
import ru.bookingsystem.entity.User;
import ru.bookingsystem.repository.BookingRepository;
import ru.bookingsystem.repository.RoomRepository;
import ru.bookingsystem.repository.UserRepository;
import ru.bookingsystem.support.AbstractIntegrationTest;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
class BookingSchedulerIntegrationTest extends AbstractIntegrationTest {

    @Autowired BookingScheduler bookingScheduler;
    @Autowired BookingRepository bookingRepository;
    @Autowired RoomRepository roomRepository;
    @Autowired UserRepository userRepository;
    @Autowired EntityManager entityManager;

    @Test
    void autoCompleteBookings_shouldMarkPastConfirmedBookingsAsCompleted() {
        User user = new User();
        user.setUsername("scheduler-user");
        user.setEmail("scheduler-user@example.com");
        user.setPassword("password");
        user.setRole(User.Role.USER);
        userRepository.save(user);

        Room room = new Room();
        room.setName("Scheduler Room");
        room.setCapacity(5);
        room.setStatus(Room.Status.AVAILABLE);
        roomRepository.save(room);

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setRoom(room);
        booking.setStartTime(Instant.now().minus(3, ChronoUnit.HOURS));
        booking.setEndTime(Instant.now().minus(1, ChronoUnit.HOURS));
        booking.setBookingStatus(Booking.BookingStatus.CONFIRMED);
        bookingRepository.save(booking);

        bookingScheduler.autoCompleteBookings();
        entityManager.flush();
        entityManager.clear();

        Booking updated = bookingRepository.findById(booking.getId()).orElseThrow();
        assertEquals(Booking.BookingStatus.COMPLETED, updated.getBookingStatus());
    }
}
