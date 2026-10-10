package com.foodcourt.user.infrastructure.input.rest.mapper;

import com.foodcourt.user.domain.model.AccessToken;
import com.foodcourt.user.domain.model.Credentials;
import com.foodcourt.user.infrastructure.input.rest.dto.LoginRequest;
import com.foodcourt.user.infrastructure.input.rest.dto.LoginResponse;

/** Copies login fields. Applies no credential check and does not sign a token. */
public final class LoginRestMapper {

    public Credentials toCredentials(LoginRequest request) {
        return new Credentials(request.email(), request.password());
    }

    public LoginResponse toResponse(AccessToken token) {
        return new LoginResponse("Bearer", token.value(), token.expiresInSeconds(), token.userId(), token.role());
    }
}
