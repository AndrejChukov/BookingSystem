package ru.bookingsystem.mapper;

import org.junit.jupiter.api.Test;
import ru.bookingsystem.dto.request.BookingRequestDTO;
import ru.bookingsystem.dto.response.BookingResponseDTO;
import ru.bookingsystem.entity.Booking;
import ru.bookingsystem.entity.Room;
import ru.bookingsystem.entity.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BookingMapperTest {

    private final BookingMapper mapper = new BookingMapperImpl();

    @Test
    void toEntity_shouldMapTimesAndIgnoreRelations() {
        Instant start = Instant.now().plus(1, ChronoUnit.HOURS);
        Instant end = start.plus(1, ChronoUnit.HOURS);
        BookingRequestDTO request = new BookingRequestDTO(42L, start, end);

        Booking entity = mapper.toEntity(request);

        assertEquals(start, entity.getStartTime());
        assertEquals(end, entity.getEndTime());
        assertNull(entity.getUser(), "user must be ignored by the mapper");
        assertNull(entity.getRoom(), "room must be ignored by the mapper");
        assertNull(entity.getBookingStatus(), "status must be ignored by the mapper");
    }

    @Test
    void toEntity_shouldReturnNull_whenRequestIsNull() {
        assertNull(mapper.toEntity(null));
    }

    @Test
    void toResponse_shouldMapAllExposedFields() {
        Instant start = Instant.now();
        Instant end = start.plus(2, ChronoUnit.HOURS);

        Booking booking = new Booking();
        booking.setId(7L);
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setBookingStatus(Booking.BookingStatus.CONFIRMED);
        booking.setUser(new User());
        booking.setRoom(new Room());

        BookingResponseDTO response = mapper.toResponse(booking);

        assertEquals(7L, response.id());
        assertEquals(start, response.startTime());
        assertEquals(end, response.endTime());
        assertEquals(Booking.BookingStatus.CONFIRMED, response.bookingStatus());
    }

    @Test
    void toResponse_shouldReturnNull_whenBookingIsNull() {
        assertNull(mapper.toResponse(null));
    }
}
