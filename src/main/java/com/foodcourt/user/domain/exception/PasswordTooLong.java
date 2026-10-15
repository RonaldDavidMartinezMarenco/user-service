package com.foodcourt.user.domain.exception;

/** The password is longer than the 72 bytes bcrypt can store. Mapped to HTTP 400. */
public final class PasswordTooLong extends RuntimeException {

    public PasswordTooLong() {
        super("The password must not exceed 72 bytes");
    }
}
