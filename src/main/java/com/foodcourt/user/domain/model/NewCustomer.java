package com.foodcourt.user.domain.model;

/**
 * Input of customer registration. {@code password} is still the raw value;
 * the use case replaces it with a bcrypt hash before persistence.
 * {@code roleId} must be the customer role. The customer chooses neither a restaurant nor a birth date.
 */
public record NewCustomer(
        String firstName,
        String lastName,
        String identityDocument,
        String phone,
        String email,
        String password,
        Short roleId) {
}
