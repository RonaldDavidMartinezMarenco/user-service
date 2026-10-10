package com.foodcourt.user.domain.exception;

/** The startup administrator does not satisfy the account shape. Startup fails. */
public final class InvalidAdministratorAccount extends RuntimeException {

    public InvalidAdministratorAccount(String message) {
        super(message);
    }
}
