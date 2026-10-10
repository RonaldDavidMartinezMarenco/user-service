package com.foodcourt.user.domain.model;

/** Email and raw password presented at login. The password is not stored. */
public record Credentials(String email, String password) {
}
