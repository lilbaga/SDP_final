package rental.util;

import java.util.Locale;

/** Formats amounts in one single place, so every class prints money in the same way. */
public final class Money {

    private static final String CURRENCY = "USD";

    private Money() {
    }

    public static String format(double amount) {
        return String.format(Locale.US, "%.2f %s", amount, CURRENCY);
    }
}
