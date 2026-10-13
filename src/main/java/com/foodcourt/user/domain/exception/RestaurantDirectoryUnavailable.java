package com.foodcourt.user.domain.exception;

/** plaza-service did not answer, timed out, or answered with an unexpected status. Mapped to HTTP 503. */
public final class RestaurantDirectoryUnavailable extends RuntimeException {

    public RestaurantDirectoryUnavailable() {
        super("The plaza service is unavailable");
    }
}
