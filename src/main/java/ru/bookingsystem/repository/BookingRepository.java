package ru.bookingsystem.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.bookingsystem.entity.Booking;

import java.time.Instant;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("""
    SELECT b FROM Booking b
    WHERE b.user.id = :userId
    """)
    List<Booking> findAllByUserId(Long userId);

    @Query("""
    SELECT COUNT(b) > 0 FROM Booking b
    WHERE b.room.id = :roomId
    AND b.bookingStatus = 'CONFIRMED'
    AND (b.startTime < :endTime AND b.endTime > :startTime)
    """)
    boolean existsOverlappingBookings(Long roomId, Instant startTime, Instant endTime);

    @Modifying
    @Query("""
    UPDATE Booking b Set b.bookingStatus = 'COMPLETED'
    WHERE b.endTime < :now AND b.bookingStatus = 'CONFIRMED'
    """)
    int updateCompletedBookings(Instant now);

}
