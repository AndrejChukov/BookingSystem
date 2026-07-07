package ru.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "bookings")
public class Booking {
    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "bookings_seq")
    @SequenceGenerator(name = "bookings_seq_gen", sequenceName = "bookings_seq", allocationSize = 50)
    private Long id;
    @ManyToOne
    private User user;
    @ManyToOne
    private Room room;
    private Instant startTime;
    private Instant endTime;
    @Enumerated(value = EnumType.STRING)
    private BookingStatus bookingStatus;

    public enum BookingStatus {
        CONFIRMED, CANCELLED, COMPLETED
    }
}
