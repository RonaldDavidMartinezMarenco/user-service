package com.foodcourt.user.infrastructure.input.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Body of {@code POST /login}. A password outside 8 to 72 characters fails here,
 * with the same bounds as creating an owner.
 */
public record LoginRequest(
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 72) String password) {
}
