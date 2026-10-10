package com.foodcourt.user.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodcourt.user.domain.exception.InvalidCredentials;
import com.foodcourt.user.domain.model.AccessToken;
import com.foodcourt.user.domain.model.Credentials;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.AccessTokenIssuer;
import com.foodcourt.user.domain.spi.PasswordHasher;
import com.foodcourt.user.domain.spi.UserRepository;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    private static final String RAW_PASSWORD = "secret12";

    @Mock
    private UserRepository users;

    @Mock
    private PasswordHasher passwords;

    @Mock
    private AccessTokenIssuer tokens;

    private LoginUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new LoginUseCase(users, passwords, tokens);
    }

    @Test
    void loginIssuesATokenForAMatchingPassword() {
        User account = account();
        AccessToken issued = new AccessToken("signed", 28800L, 15L, Role.OWNER);
        when(users.findByEmail("ana@example.com")).thenReturn(Optional.of(account));
        when(passwords.matches(RAW_PASSWORD, "hashed-secret12")).thenReturn(true);
        when(tokens.issue(account)).thenReturn(issued);

        AccessToken token = useCase.login(new Credentials("Ana@Example.com", RAW_PASSWORD));

        assertThat(token).isEqualTo(issued);
        verify(users).findByEmail("ana@example.com");
    }

    @Test
    void loginRejectsAWrongPassword() {
        when(users.findByEmail("ana@example.com")).thenReturn(Optional.of(account()));
        when(passwords.matches(RAW_PASSWORD, "hashed-secret12")).thenReturn(false);

        assertThatThrownBy(() -> useCase.login(new Credentials("ana@example.com", RAW_PASSWORD)))
                .isInstanceOf(InvalidCredentials.class)
                .hasMessage("Invalid email or password");
        verify(tokens, never()).issue(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void loginRejectsAnUnknownEmailWithTheSameFailure() {
        when(users.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.login(new Credentials("missing@example.com", RAW_PASSWORD)))
                .isInstanceOf(InvalidCredentials.class)
                .hasMessage("Invalid email or password");
        verify(passwords).matchesUnknown(RAW_PASSWORD);
        verify(tokens, never()).issue(org.mockito.ArgumentMatchers.any());
    }

    private static User account() {
        return new User(
                15L,
                "María",
                "Ana2",
                "1234567890",
                "+573005698325",
                LocalDate.of(1990, 5, 1),
                "ana@example.com",
                "hashed-secret12",
                Role.OWNER,
                null);
    }
}
