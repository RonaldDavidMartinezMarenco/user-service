package com.foodcourt.user.domain.exception;

/** plaza-service has no restaurant with this id. Mapped to HTTP 404. */
public final class RestaurantNotFound extends RuntimeException {

    public RestaurantNotFound() {
        super("Restaurant not found");
    }
}
