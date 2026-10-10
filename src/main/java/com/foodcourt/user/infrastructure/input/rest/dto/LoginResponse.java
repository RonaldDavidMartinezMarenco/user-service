package com.foodcourt.user.infrastructure.input.rest.dto;

import com.foodcourt.user.domain.model.Role;

/**
 * Access token returned by login. The raw password and the bcrypt hash are omitted.
 */
public record LoginResponse(
        String tokenType,
        String accessToken,
        long expiresIn,
        long userId,
        Role role) {
}
