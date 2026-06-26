package ru.bookingsystem.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.bookingsystem.repository.BookingRepository;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingScheduler {

    private final BookingRepository bookingRepository;

    @Transactional
    @Scheduled(fixedRate = 15, timeUnit = TimeUnit.MINUTES)
    public void autoCompleteBookings() {
        log.info("Automated booking completion task started.");
        int updated = bookingRepository.updateCompletedBookings(Instant.now());
        log.info("Automated booking completion task updated {} bookings to COMPLETED status.", updated);
    }

}
