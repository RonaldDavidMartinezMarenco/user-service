package com.foodcourt.user.infrastructure.input.rest.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Body of {@code POST /customers}. Shape checks fail here, before the use case.
 * The use case rejects a {@code roleId} other than the customer role.
 * Jackson 3 ignores unknown properties by default, so {@code rejectUnknown} is what fails the read.
 * A customer cannot send a restaurant, a birth date, or a role name.
 */
@JsonIgnoreProperties(ignoreUnknown = false)
public record CreateCustomerRequest(
        @NotBlank @Size(max = 80) @Pattern(regexp = "^(?=.*\\p{L})[\\p{L}\\p{N} &'.,-]+$") String firstName,
        @NotBlank @Size(max = 80) @Pattern(regexp = "^(?=.*\\p{L})[\\p{L}\\p{N} &'.,-]+$") String lastName,
        @NotBlank @Size(max = 20) @Pattern(regexp = "^[0-9]+$") String identityDocument,
        @NotBlank @Size(min = 7, max = 13) @Pattern(regexp = "^\\+?[0-9]+$") String phone,
        @NotBlank @Email @Size(max = 254) String email,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotNull Short roleId) {

    @JsonAnySetter
    void rejectUnknown(String name, Object ignored) {
        throw new IllegalArgumentException("Unknown field: " + name);
    }
}
