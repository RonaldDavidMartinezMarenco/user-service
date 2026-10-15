package com.foodcourt.user.infrastructure.input.rest.dto;

import com.foodcourt.user.domain.model.Role;

/**
 * Customer returned after registration. The raw password and the bcrypt hash are omitted.
 * A customer has no birth date and belongs to no restaurant.
 */
public record CustomerResponse(
        Long id,
        String firstName,
        String lastName,
        String identityDocument,
        String phone,
        String email,
        Role role) {
}
