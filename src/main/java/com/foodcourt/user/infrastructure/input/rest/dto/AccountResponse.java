package com.foodcourt.user.infrastructure.input.rest.dto;

import com.foodcourt.user.domain.model.Role;

/**
 * Account summary returned by id. The raw password and the bcrypt hash are omitted.
 * {@code restaurantId} is set only for an employee. Every other role carries null.
 */
public record AccountResponse(Long id, Role role, Long restaurantId) {
}
