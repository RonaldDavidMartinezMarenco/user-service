package com.foodcourt.user.infrastructure.input.rest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Body of {@code POST /employees}. Shape checks fail here, before the use case.
 * The use case rejects a {@code roleId} other than the employee role and a restaurant the caller does not own.
 */
public record CreateEmployeeRequest(
        @NotBlank @Size(max = 80) @Pattern(regexp = "^(?=.*\\p{L})[\\p{L}\\p{N} &'.,-]+$") String firstName,
        @NotBlank @Size(max = 80) @Pattern(regexp = "^(?=.*\\p{L})[\\p{L}\\p{N} &'.,-]+$") String lastName,
        @NotBlank @Pattern(regexp = "^[0-9]+$") String identityDocument,
        @NotBlank @Size(max = 13) @Pattern(regexp = "^\\+?[0-9]+$") String phone,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotNull Short roleId,
        @NotNull @Positive Long restaurantId) {
}
