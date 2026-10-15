package com.foodcourt.user.domain.model;

import java.nio.charset.StandardCharsets;

import com.foodcourt.user.domain.exception.PasswordTooLong;

/**
 * A password bcrypt can store. Bcrypt reads at most {@link #MAX_BYTES} UTF-8 bytes
 * and throws when the value is longer, which would otherwise become HTTP 500.
 * This is a class, not a record, so a {@code toString} never prints the password.
 */
public final class PasswordPolicy {

    public static final int MAX_BYTES = 72;

    private PasswordPolicy() {
    }

    public static void requireStorable(String rawPassword) {
        if (rawPassword.getBytes(StandardCharsets.UTF_8).length > MAX_BYTES) {
            throw new PasswordTooLong();
        }
    }
}
