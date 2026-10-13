package com.foodcourt.user.infrastructure.input.rest.dto;

import com.foodcourt.user.domain.model.Role;

/**
 * Employee returned after creation. The raw password and the bcrypt hash are omitted.
 */
public record EmployeeResponse(
        Long id,
        String firstName,
        String lastName,
        String identityDocument,
        String phone,
        String email,
        Role role,
        Long restaurantId) {
}
