package com.foodcourt.user.infrastructure.input.rest.dto;

import java.time.LocalDate;

import com.foodcourt.user.domain.model.Role;

/**
 * Owner returned after creation. The raw password and the bcrypt hash are omitted.
 */
public record OwnerResponse(
        Long id,
        String firstName,
        String lastName,
        String identityDocument,
        String phone,
        LocalDate birthDate,
        String email,
        Role role) {
}
