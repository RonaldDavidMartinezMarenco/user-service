package com.foodcourt.user.domain.model;

/**
 * Account that called the current request. The id is {@code app_user.id}
 * and the role is the one carried by the access token.
 */
public record Caller(long id, Role role) {
}
