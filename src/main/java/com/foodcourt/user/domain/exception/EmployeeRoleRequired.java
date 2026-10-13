package com.foodcourt.user.domain.exception;

/** The requested {@code roleId} is not the employee role. Mapped to HTTP 400. */
public final class EmployeeRoleRequired extends RuntimeException {

    public EmployeeRoleRequired() {
        super("The role must be EMPLOYEE");
    }
}
