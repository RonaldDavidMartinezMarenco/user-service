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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodcourt.user.domain.exception.AccessDenied;
import com.foodcourt.user.domain.exception.UserNotFound;
import com.foodcourt.user.domain.model.Caller;
import com.foodcourt.user.domain.model.CustomerPhone;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.UserRepository;

@ExtendWith(MockitoExtension.class)
class FindCustomerPhoneUseCaseTest {

    private static final Caller EMPLOYEE = new Caller(5L, Role.EMPLOYEE);
    private static final Caller ADMINISTRATOR = new Caller(1L, Role.ADMINISTRATOR);
    private static final String PHONE = "+573011590640";

    @Mock
    private UserRepository users;

    private FindCustomerPhoneUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FindCustomerPhoneUseCase(users);
    }

    @Test
    void findReturnsThePhoneOfACustomerToAnEmployee() {
        when(users.findById(9L)).thenReturn(Optional.of(account(9L, Role.CUSTOMER)));

        CustomerPhone phone = useCase.find(EMPLOYEE, 9L);

        assertThat(phone.phone()).isEqualTo(PHONE);
    }

    @Test
    void findReturnsThePhoneOfACustomerToAnAdministrator() {
        when(users.findById(9L)).thenReturn(Optional.of(account(9L, Role.CUSTOMER)));

        assertThat(useCase.find(ADMINISTRATOR, 9L).phone()).isEqualTo(PHONE);
    }

    @Test
    void findReturnsTheCallersOwnPhone() {
        when(users.findById(9L)).thenReturn(Optional.of(account(9L, Role.CUSTOMER)));

        assertThat(useCase.find(new Caller(9L, Role.CUSTOMER), 9L).phone()).isEqualTo(PHONE);
    }

    @Test
    void findRejectsAMissingAccount() {
        when(users.findById(9L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.find(EMPLOYEE, 9L))
                .isInstanceOf(UserNotFound.class)
                .hasMessage("No account exists for this id");
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = {"ADMINISTRATOR", "OWNER", "EMPLOYEE"})
    void findRejectsAnAccountThatIsNotACustomer(Role role) {
        when(users.findById(2L)).thenReturn(Optional.of(account(2L, role)));

        assertThatThrownBy(() -> useCase.find(EMPLOYEE, 2L))
                .isInstanceOf(UserNotFound.class)
                .hasMessage("No account exists for this id");
    }

    @Test
    void findRejectsAnotherAccountForACustomer() {
        assertThatThrownBy(() -> useCase.find(new Caller(9L, Role.CUSTOMER), 10L))
                .isInstanceOf(AccessDenied.class);
        verify(users, never()).findById(10L);
    }

    @Test
    void findRejectsAnotherAccountForAnOwner() {
        assertThatThrownBy(() -> useCase.find(new Caller(2L, Role.OWNER), 9L)).isInstanceOf(AccessDenied.class);
        verify(users, never()).findById(9L);
    }

    private static User account(long id, Role role) {
        return new User(
                id,
                "Ronald",
                "Martínez",
                "3011590640",
                PHONE,
                null,
                "ronald.martinez@example.com",
                "hashed",
                role,
                null);
    }
}
