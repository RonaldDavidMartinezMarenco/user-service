package com.foodcourt.user.domain.model;

/**
 * First administrator supplied at startup. {@code birthDate} and {@code restaurantId}
 * stay null; this account is not an owner and does not belong to a restaurant.
 */
public record NewAdministrator(
        String firstName,
        String lastName,
        String identityDocument,
        String phone,
        String email,
        String password) {
}
