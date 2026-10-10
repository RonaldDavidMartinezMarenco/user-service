package com.foodcourt.user.domain.exception;

/** The email is unknown or the password does not match. Mapped to HTTP 401. */
public final class InvalidCredentials extends RuntimeException {

    public InvalidCredentials() {
        super("Invalid email or password");
    }
}
