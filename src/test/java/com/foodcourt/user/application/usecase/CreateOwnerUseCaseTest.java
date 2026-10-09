package com.foodcourt.user.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodcourt.user.domain.exception.EmailAlreadyRegistered;
import com.foodcourt.user.domain.exception.IdentityDocumentAlreadyRegistered;
import com.foodcourt.user.domain.exception.UnderageOwner;
import com.foodcourt.user.domain.model.NewOwner;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.PasswordHasher;
import com.foodcourt.user.domain.spi.UserRepository;

@ExtendWith(MockitoExtension.class)
class CreateOwnerUseCaseTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-10-08T15:00:00Z"), ZoneOffset.UTC);
    private static final String RAW_PASSWORD = "secret12";
    private static final String PASSWORD_HASH = "hashed-secret12";

    @Mock
    private UserRepository users;

    @Mock
    private PasswordHasher passwords;

    private CreateOwnerUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateOwnerUseCase(users, passwords, CLOCK);
    }

    @Test
    void createStoresAnOwnerWithAHashAndALowerCaseEmail() {
        NewOwner owner = ownerBornOn(LocalDate.of(1990, 5, 1), "Ana@Example.com");
        when(passwords.hash(RAW_PASSWORD)).thenReturn(PASSWORD_HASH);
        when(users.save(any(User.class))).thenAnswer(invocation -> withId(invocation.getArgument(0)));

        User created = useCase.create(owner);

        ArgumentCaptor<User> stored = ArgumentCaptor.forClass(User.class);
        verify(users).save(stored.capture());
        verify(users).existsByEmail("ana@example.com");
        assertThat(stored.getValue().id()).isNull();
        assertThat(stored.getValue().role()).isEqualTo(Role.OWNER);
        assertThat(stored.getValue().restaurantId()).isNull();
        assertThat(stored.getValue().email()).isEqualTo("ana@example.com");
        assertThat(stored.getValue().passwordHash()).isEqualTo(PASSWORD_HASH);
        assertThat(stored.getValue().passwordHash()).isNotEqualTo(RAW_PASSWORD);
        assertThat(stored.getValue().birthDate()).isEqualTo(LocalDate.of(1990, 5, 1));
        assertThat(created.id()).isEqualTo(15L);
        assertThat(created.role()).isEqualTo(Role.OWNER);
    }

    @Test
    void createAcceptsTheEighteenthBirthday() {
        NewOwner owner = ownerBornOn(LocalDate.of(2008, 10, 8), "adult@example.com");
        when(passwords.hash(RAW_PASSWORD)).thenReturn(PASSWORD_HASH);
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = useCase.create(owner);

        assertThat(created.role()).isEqualTo(Role.OWNER);
        assertThat(created.birthDate()).isEqualTo(LocalDate.of(2008, 10, 8));
        verify(users).save(any(User.class));
    }

    @Test
    void createRejectsSeventeenCompletedYears() {
        NewOwner owner = ownerBornOn(LocalDate.of(2008, 10, 9), "young@example.com");

        assertThatThrownBy(() -> useCase.create(owner)).isInstanceOf(UnderageOwner.class);
        verify(users, never()).save(any());
        verify(passwords, never()).hash(any());
    }

    @Test
    void createRejectsAFutureBirthDate() {
        NewOwner owner = ownerBornOn(LocalDate.of(2027, 1, 1), "future@example.com");

        assertThatThrownBy(() -> useCase.create(owner)).isInstanceOf(UnderageOwner.class);
        verify(users, never()).save(any());
    }

    @Test
    void createRejectsADuplicateEmail() {
        NewOwner owner = ownerBornOn(LocalDate.of(1990, 5, 1), "Ana@Example.com");
        when(users.existsByEmail("ana@example.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase.create(owner)).isInstanceOf(EmailAlreadyRegistered.class);
        verify(users, never()).save(any());
        verify(passwords, never()).hash(any());
    }

    @Test
    void createRejectsADuplicateIdentityDocument() {
        NewOwner owner = ownerBornOn(LocalDate.of(1990, 5, 1), "ana@example.com");
        when(users.existsByIdentityDocument("1234567890")).thenReturn(true);

        assertThatThrownBy(() -> useCase.create(owner))
                .isInstanceOf(IdentityDocumentAlreadyRegistered.class);
        verify(users, never()).save(any());
        verify(passwords, never()).hash(any());
    }

    private static NewOwner ownerBornOn(LocalDate birthDate, String email) {
        return new NewOwner(
                "María",
                "Ana2",
                "1234567890",
                "+573005698325",
                birthDate,
                email,
                RAW_PASSWORD);
    }

    private static User withId(User user) {
        return new User(
                15L,
                user.firstName(),
                user.lastName(),
                user.identityDocument(),
                user.phone(),
                user.birthDate(),
                user.email(),
                user.passwordHash(),
                user.role(),
                user.restaurantId());
    }
}
