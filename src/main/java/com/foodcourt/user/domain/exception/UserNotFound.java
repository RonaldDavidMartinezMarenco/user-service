package com.foodcourt.user.domain.exception;

/** No {@code app_user} row exists for the requested id. Mapped to HTTP 404. */
public final class UserNotFound extends RuntimeException {

    public UserNotFound() {
        super("No account exists for this id");
    }
}
