package com.foodcourt.user.infrastructure.output.jpa.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.foodcourt.user.domain.exception.EmailAlreadyRegistered;
import com.foodcourt.user.domain.exception.IdentityDocumentAlreadyRegistered;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.infrastructure.output.jpa.entity.UserEntity;
import com.foodcourt.user.infrastructure.output.jpa.repository.UserJpaRepository;

@ExtendWith(MockitoExtension.class)
class UserRepositoryAdapterTest {

    @Mock
    private UserJpaRepository accounts;

    private UserRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new UserRepositoryAdapter(accounts);
    }

    @Test
    void saveReturnsTheStoredOwner() {
        when(accounts.saveAndFlush(any(UserEntity.class))).thenAnswer(invocation -> {
            UserEntity row = invocation.getArgument(0);
            return new UserEntity(
                    15L,
                    row.getFirstName(),
                    row.getLastName(),
                    row.getIdentityDocument(),
                    row.getPhone(),
                    row.getBirthDate(),
                    row.getEmail(),
                    row.getPasswordHash(),
                    row.getRoleId(),
                    row.getRestaurantId());
        });

        User stored = adapter.save(owner());

        assertThat(stored.id()).isEqualTo(15L);
        assertThat(stored.role()).isEqualTo(Role.OWNER);
        assertThat(stored.restaurantId()).isNull();
        assertThat(stored.passwordHash()).isEqualTo("hashed-secret12");
        assertThat(stored.email()).isEqualTo("ana@example.com");
    }

    @Test
    void existsByEmailDelegatesToTheRow() {
        when(accounts.existsByEmail("ana@example.com")).thenReturn(true);

        assertThat(adapter.existsByEmail("ana@example.com")).isTrue();
        verify(accounts).existsByEmail("ana@example.com");
    }

    @Test
    void existsByIdentityDocumentDelegatesToTheRow() {
        when(accounts.existsByIdentityDocument("1234567890")).thenReturn(false);

        assertThat(adapter.existsByIdentityDocument("1234567890")).isFalse();
    }

    @Test
    void saveRejectsADuplicateEmail() {
        when(accounts.saveAndFlush(any(UserEntity.class)))
                .thenThrow(unique("duplicate key value violates unique constraint \"app_user_email_key\""));

        assertThatThrownBy(() -> adapter.save(owner())).isInstanceOf(EmailAlreadyRegistered.class);
    }

    @Test
    void saveRejectsADuplicateIdentityDocument() {
        when(accounts.saveAndFlush(any(UserEntity.class))).thenThrow(unique(
                "duplicate key value violates unique constraint \"app_user_identity_document_key\""));

        assertThatThrownBy(() -> adapter.save(owner()))
                .isInstanceOf(IdentityDocumentAlreadyRegistered.class);
    }

    @Test
    void saveRethrowsAUniqueViolationItCannotClassify() {
        DataIntegrityViolationException failure = unique("duplicate key value violates unique constraint \"other\"");
        when(accounts.saveAndFlush(any(UserEntity.class))).thenThrow(failure);

        assertThatThrownBy(() -> adapter.save(owner())).isSameAs(failure);
    }

    @Test
    void saveRethrowsAnIntegrityFailureThatIsNotUnique() {
        DataIntegrityViolationException failure = new DataIntegrityViolationException(
                "null",
                new SQLException("null value", "23502"));
        when(accounts.saveAndFlush(any(UserEntity.class))).thenThrow(failure);

        assertThatThrownBy(() -> adapter.save(owner())).isSameAs(failure);
    }

    private static DataIntegrityViolationException unique(String message) {
        return new DataIntegrityViolationException("conflict", new SQLException(message, UserRepositoryAdapter.UNIQUE_VIOLATION));
    }

    private static User owner() {
        return new User(
                null,
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
