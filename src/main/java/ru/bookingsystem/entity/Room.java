package ru.bookingsystem.entity;

import jakarta.persistence.*;
import lombok.Data;

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
    private List<Equipment> equipment;

    @Enumerated(EnumType.STRING)
    private Status status;

    enum Status {
        AVAILABLE, OCCUPIED, MAINTENANCE
    }
}
