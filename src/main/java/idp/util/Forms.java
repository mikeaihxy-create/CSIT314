package idp.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public final class Forms {

    private Forms() {
    }

    public static long id(String text) {
        if (Texts.isBlank(text)) {
            return 0L;
        }
        try {
            return Long.parseLong(text.trim());
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }

    public static BigDecimal money(String text) {
        if (Texts.isBlank(text)) {
            return null;
        }
        try {
            return new BigDecimal(text.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static LocalDate date(String text) {
        return Clock.parseDate(text);
    }

    public static List<String> images(String text) {
        List<String> urls = new ArrayList<>();
        if (Texts.isBlank(text)) {
            return urls;
        }
        for (String part : text.split("[,\\r\\n]+")) {
            if (Texts.isPresent(part)) {
                urls.add(part.trim());
            }
        }
        return urls;
    }

    public static List<String> list(String text) {
        return images(text);
    }
}
