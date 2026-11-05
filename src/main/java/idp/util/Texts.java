package idp.util;

import java.util.Collection;
import java.util.Locale;

public final class Texts {

    private Texts() {
    }

    public static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    public static boolean isPresent(String value) {
        return !isBlank(value);
    }

    public static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    public static String lower(String value) {
        return trim(value).toLowerCase(Locale.ROOT);
    }

    public static boolean containsIgnoreCase(String haystack, String needle) {
        if (isBlank(haystack)) {
            return false;
        }
        return haystack.toLowerCase(Locale.ROOT).contains(lower(needle));
    }

    public static boolean anyPresent(String... values) {
        for (String value : values) {
            if (isPresent(value)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isEmpty(Collection<?> values) {
        return values == null || values.isEmpty();
    }
}
