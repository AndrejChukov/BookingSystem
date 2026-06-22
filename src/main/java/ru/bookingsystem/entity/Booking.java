package ru.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Room room;
    private Instant startTime;
    private Instant endTime;
    private BookingStatus bookingStatus;

    public enum BookingStatus {
        CONFIRMED, CANCELLED, COMPLETED
    }
}
