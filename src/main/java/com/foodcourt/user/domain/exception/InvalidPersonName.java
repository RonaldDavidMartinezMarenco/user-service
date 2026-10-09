package com.foodcourt.user.domain.exception;

/** A first or last name fails the stored shape. Mapped to HTTP 400. */
public final class InvalidPersonName extends RuntimeException {

    public InvalidPersonName() {
        super("The name must contain a letter and only the allowed characters");
    }
}
