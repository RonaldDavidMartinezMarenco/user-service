package com.foodcourt.user.domain.model;

import java.util.regex.Pattern;

import com.foodcourt.user.domain.exception.InvalidPersonName;

/**
 * First or last name stored without surrounding spaces and with single spaces inside.
 * At least two characters, at most {@link #MAX}, one letter, and only letters, digits,
 * spaces, and {@code - ' . , &}.
 */
public record PersonName(String value) {

    public static final int MAX = 80;

    private static final int MIN = 2;
    private static final Pattern LETTER = Pattern.compile("\\p{L}");
    private static final Pattern ALLOWED = Pattern.compile("^[\\p{L}\\p{N} &'.,-]+$");

    public PersonName {
        String normalized = collapse(value);
        if (!allowed(normalized)) {
            throw new InvalidPersonName();
        }
        value = normalized;
    }

    private static String collapse(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().replaceAll(" {2,}", " ");
    }

    private static boolean allowed(String normalized) {
        return normalized.length() >= MIN
                && normalized.length() <= MAX
                && LETTER.matcher(normalized).find()
                && ALLOWED.matcher(normalized).matches();
    }
}
