package com.foodcourt.user.application.usecase;

import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.api.Login;
import com.foodcourt.user.domain.exception.InvalidCredentials;
import com.foodcourt.user.domain.model.AccessToken;
import com.foodcourt.user.domain.model.Credentials;
import com.foodcourt.user.domain.model.EmailAddress;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.AccessTokenIssuer;
import com.foodcourt.user.domain.spi.PasswordHasher;
import com.foodcourt.user.domain.spi.UserRepository;

/**
 * Exchanges email and password for an access token.
 * An unknown email and a wrong password raise the same {@link InvalidCredentials}.
 * A rejection does not issue a token.
 */
@Service
public final class LoginUseCase implements Login {

    private final UserRepository users;
    private final PasswordHasher passwords;
    private final AccessTokenIssuer tokens;

    public LoginUseCase(UserRepository users, PasswordHasher passwords, AccessTokenIssuer tokens) {
        this.users = users;
        this.passwords = passwords;
        this.tokens = tokens;
    }

    @Override
    public AccessToken login(Credentials credentials) {
        return users.findByEmail(normalized(credentials))
                .map(user -> authenticated(credentials.password(), user))
                .orElseGet(() -> unknown(credentials.password()));
    }

    private AccessToken authenticated(String rawPassword, User user) {
        if (!passwords.matches(rawPassword, user.passwordHash())) {
            throw new InvalidCredentials();
        }
        return tokens.issue(user); // sign the token and return it
    }

    private AccessToken unknown(String rawPassword) {
        passwords.matchesUnknown(rawPassword);
        throw new InvalidCredentials();
    }

    private static String normalized(Credentials credentials) {
        return new EmailAddress(credentials.email()).value();
    }
}
