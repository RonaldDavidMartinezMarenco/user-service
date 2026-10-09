package com.foodcourt.user.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodcourt.user.domain.exception.UserNotFound;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.UserRepository;

@ExtendWith(MockitoExtension.class)
class FindAccountUseCaseTest {

    @Mock
    private UserRepository users;

    private FindAccountUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FindAccountUseCase(users);
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"OWNER", "CUSTOMER"})
    void findReturnsTheStoredRole(Role role) {
        when(users.findById(15L)).thenReturn(Optional.of(account(role)));

        User found = useCase.find(15L);

        assertThat(found.id()).isEqualTo(15L);
        assertThat(found.role()).isEqualTo(role);
    }

    @Test
    void findRejectsAMissingAccount() {
        when(users.findById(15L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.find(15L)).isInstanceOf(UserNotFound.class);
    }

    private static User account(Role role) {
        return new User(
                15L,
                "María",
                "Ana2",
                "1234567890",
                "+573005698325",
                LocalDate.of(1990, 5, 1),
                "ana@example.com",
                "hashed-secret12",
                role,
                null);
    }
}
