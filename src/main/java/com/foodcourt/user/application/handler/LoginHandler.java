package com.foodcourt.user.application.handler;

import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.api.Login;
import com.foodcourt.user.domain.model.AccessToken;
import com.foodcourt.user.domain.model.Credentials;

/**
 * Entry the controller calls. Validation stays on the request DTO and the
 * credential check stays in {@link Login}; this type only forwards the call.
 */
@Service
public final class LoginHandler {

    private final Login login;

    public LoginHandler(Login login) {
        this.login = login;
    }

    public AccessToken login(Credentials credentials) {
        return login.login(credentials);
    }
}
