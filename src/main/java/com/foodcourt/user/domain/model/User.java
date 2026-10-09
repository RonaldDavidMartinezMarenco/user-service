package com.foodcourt.user.domain.model;

import java.time.LocalDate;

/**
 * Persisted account. {@code passwordHash} is the bcrypt hash, never the raw password.
 * {@code id} is null until PostgreSQL assigns it.
 * {@code restaurantId} stays null for an owner; only an employee belongs to a restaurant.
 */
public record User(
        Long id,
        String firstName,
        String lastName,
        String identityDocument,
        String phone,
        LocalDate birthDate,
        String email,
        String passwordHash,
        Role role,
        Long restaurantId) {
}
