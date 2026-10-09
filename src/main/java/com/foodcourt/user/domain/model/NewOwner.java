package com.foodcourt.user.domain.model;

import java.time.LocalDate;

/**
 * Input of owner creation. {@code password} is still the raw value;
 * the use case replaces it with a bcrypt hash before persistence.
 * The caller cannot choose the role or a restaurant.
 */
public record NewOwner(
        String firstName,
        String lastName,
        String identityDocument,
        String phone,
        LocalDate birthDate,
        String email,
        String password) {
}
