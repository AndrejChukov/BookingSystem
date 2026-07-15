package ru.bookingsystem.support;

import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public final class BookingTestTimes {

    private static final ZoneId ZONE = ZoneId.of("Europe/Moscow");
    private static final LocalTime WORK_START = LocalTime.of(8, 0);
    private static final LocalTime WORK_END = LocalTime.of(21, 0);

    private BookingTestTimes() {
    }

    public static Instant workingStart() {
        ZonedDateTime now = ZonedDateTime.now(ZONE);
        ZonedDateTime candidate = now.plusHours(1).withMinute(0).withSecond(0).withNano(0);

        if (candidate.toLocalTime().isBefore(WORK_START)) {
            candidate = candidate.withHour(10).withMinute(0);
        }
        if (candidate.toLocalTime().isAfter(WORK_END.minusHours(2))) {
            candidate = candidate.plusDays(1).withHour(10).withMinute(0);
        }
        if (!candidate.toInstant().isAfter(Instant.now())) {
            candidate = candidate.plusHours(1);
        }
        return candidate.toInstant();
    }

    public static Instant workingEnd(Instant start) {
        return start.plus(1, ChronoUnit.HOURS);
    }

    public static Instant laterWorkingStart(Instant previousStart) {
        ZonedDateTime next = previousStart.atZone(ZONE).plusHours(2);
        if (next.toLocalTime().isAfter(WORK_END.minusHours(1))) {
            next = previousStart.atZone(ZONE).plusDays(1).withHour(10).withMinute(0);
        }
        return next.toInstant();
    }
}
