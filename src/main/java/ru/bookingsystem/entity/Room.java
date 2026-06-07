package ru.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Entity
@Table(name = "rooms")
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int capacity;

    @ManyToMany
    private List<Equipment> equipments;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();

    public enum Status {
        AVAILABLE, OCCUPIED, MAINTENANCE
    }
}
