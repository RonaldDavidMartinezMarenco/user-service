package com.foodcourt.user.infrastructure.input.rest.dto;

import com.foodcourt.user.domain.model.Role;

/**
 * Account summary returned by id. The raw password and the bcrypt hash are omitted,
 * along with every field plaza does not need to check the owner role.
 */
public record AccountResponse(Long id, Role role) {
}
