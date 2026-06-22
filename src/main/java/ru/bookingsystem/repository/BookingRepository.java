package ru.bookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bookingsystem.entity.Booking;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
    SELECT b FROM Bookings b
    WHERE b.user.id = :userId
    """)
    List<Booking> findAllByUserId(Long userId);

    @Query("""
    SELECT COUNT(b) > 0 FROM Bookings b
        WHERE b.room.id = roomId
        AND b.status = 'CONFIRMED'
        AND (b.startTime < :endTime AND b.endTime > :startTime)
    """)
    boolean existsOverlappingBookings(Long roomId, String startTime, String endTime);

}
