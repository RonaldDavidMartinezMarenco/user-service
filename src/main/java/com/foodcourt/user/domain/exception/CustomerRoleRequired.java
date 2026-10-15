package com.foodcourt.user.domain.exception;

/** The requested {@code roleId} is not the customer role. Mapped to HTTP 400. */
public final class CustomerRoleRequired extends RuntimeException {

    public CustomerRoleRequired() {
        super("The role must be CUSTOMER");
    }
}
