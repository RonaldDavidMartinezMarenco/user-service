package com.foodcourt.user.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodcourt.user.domain.exception.InvalidAdministratorAccount;
import com.foodcourt.user.domain.model.NewAdministrator;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.PasswordHasher;
import com.foodcourt.user.domain.spi.UserRepository;

@ExtendWith(MockitoExtension.class)
class EnsureAdministratorUseCaseTest {

    private static final String RAW_PASSWORD = "secret12";

    @Mock
    private UserRepository users;

    @Mock
    private PasswordHasher passwords;

    private EnsureAdministratorUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new EnsureAdministratorUseCase(users, passwords);
    }

    @Test
    void ensureStoresTheFirstAdministrator() {
        when(passwords.hash(RAW_PASSWORD)).thenReturn("hashed-secret12");
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        useCase.ensure(administrator(RAW_PASSWORD, "100200300", "+573005698325"));

        ArgumentCaptor<User> stored = ArgumentCaptor.forClass(User.class);
        verify(users).save(stored.capture());
        assertThat(stored.getValue().role()).isEqualTo(Role.ADMINISTRATOR);
        assertThat(stored.getValue().email()).isEqualTo("admin@example.com");
        assertThat(stored.getValue().passwordHash()).isEqualTo("hashed-secret12");
        assertThat(stored.getValue().birthDate()).isNull();
        assertThat(stored.getValue().restaurantId()).isNull();
    }

    @Test
    void ensureLeavesAnExistingAdministratorUnchanged() {
        when(users.existsByRole(Role.ADMINISTRATOR)).thenReturn(true);

        useCase.ensure(administrator(RAW_PASSWORD, "100200300", "+573005698325"));

        verify(users, never()).save(any());
        verify(passwords, never()).hash(any());
    }

    @Test
    void ensureRejectsAShortPassword() {
        assertThatThrownBy(() -> useCase.ensure(administrator("short", "100200300", "+573005698325")))
                .isInstanceOf(InvalidAdministratorAccount.class);
        verify(users, never()).save(any());
    }

    @Test
    void ensureRejectsANonNumericDocument() {
        assertThatThrownBy(() -> useCase.ensure(administrator(RAW_PASSWORD, "12A", "+573005698325")))
                .isInstanceOf(InvalidAdministratorAccount.class);
        verify(users, never()).save(any());
    }

    @Test
    void ensureRejectsAMalformedPhone() {
        assertThatThrownBy(() -> useCase.ensure(administrator(RAW_PASSWORD, "100200300", "57abc")))
                .isInstanceOf(InvalidAdministratorAccount.class);
        verify(users, never()).save(any());
    }

    private static NewAdministrator administrator(String password, String document, String phone) {
        return new NewAdministrator("Ada", "Admin", document, phone, "Admin@Example.com", password);
    }
}
