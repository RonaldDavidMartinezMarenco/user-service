package com.foodcourt.user.domain.model;

/**
 * Signed access token. {@code value} is the compact JWT.
 * The raw password and the bcrypt hash are not part of this value.
 */
public record AccessToken(String value, long expiresInSeconds, long userId, Role role) {
}
