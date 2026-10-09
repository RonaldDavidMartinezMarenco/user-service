package com.foodcourt.user.infrastructure.input.rest.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Body of {@code POST /owners}. Shape checks fail here, before the use case.
 * The client does not send a role or a restaurant id.
 */
public record CreateOwnerRequest(
        @NotBlank @Size(max = 80) @Pattern(regexp = "^(?=.*\\p{L})[\\p{L}\\p{N} &'.,-]+$") String firstName,
        @NotBlank @Size(max = 80) @Pattern(regexp = "^(?=.*\\p{L})[\\p{L}\\p{N} &'.,-]+$") String lastName,
        @NotBlank @Pattern(regexp = "^[0-9]+$") String identityDocument,
        @NotBlank @Size(max = 13) @Pattern(regexp = "^\\+?[0-9]+$") String phone,
        @NotNull LocalDate birthDate,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 72) String password) {
}
