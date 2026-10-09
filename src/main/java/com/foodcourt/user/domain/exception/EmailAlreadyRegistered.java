package com.foodcourt.user.domain.exception;

/** {@code app_user.email} is already taken. Mapped to HTTP 409. */
public final class EmailAlreadyRegistered extends RuntimeException {

    public EmailAlreadyRegistered() {
        super("Email is already registered");
    }
}
