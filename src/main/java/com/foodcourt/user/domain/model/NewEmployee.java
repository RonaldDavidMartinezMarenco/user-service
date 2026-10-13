package com.foodcourt.user.domain.model;

/**
 * Input of employee creation. {@code password} is still the raw value;
 * the use case replaces it with a bcrypt hash before persistence.
 * {@code roleId} must be the employee role, and {@code restaurantId} must belong to the caller.
 */
public record NewEmployee(
        String firstName,
        String lastName,
        String identityDocument,
        String phone,
        String email,
        String password,
        Short roleId,
        Long restaurantId) {
}
