package com.foodcourt.user.domain.exception;

/** The birth date is present, but the person has not yet completed 18 years. */
public final class UnderageOwner extends RuntimeException {

    public UnderageOwner() {
        super("The owner must be an adult");
    }
}
