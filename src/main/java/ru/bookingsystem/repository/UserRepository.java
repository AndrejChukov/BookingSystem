package ru.bookingsystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.bookingsystem.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
