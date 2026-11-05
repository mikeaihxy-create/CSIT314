package idp.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public final class Clock {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ISO_LOCAL_DATE;

    private static LocalDateTime fixed;

    private Clock() {
    }

    public static LocalDateTime now() {
        return fixed != null ? fixed : LocalDateTime.now();
    }

    public static LocalDate today() {
        return now().toLocalDate();
    }

    public static void freeze(LocalDateTime instant) {
        fixed = instant;
    }

    public static void freezeAt(String isoDate) {
        fixed = LocalDate.parse(isoDate, DATE).atTime(9, 0);
    }

    public static void useSystemClock() {
        fixed = null;
    }

    public static boolean isFrozen() {
        return fixed != null;
    }

    public static LocalDate parseDate(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(value.trim(), DATE);
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    public static String format(LocalDate date) {
        return date == null ? "" : date.format(DATE);
    }

    public static String format(LocalDateTime instant) {
        return instant == null ? "" : instant.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
