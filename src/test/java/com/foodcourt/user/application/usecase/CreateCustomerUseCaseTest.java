package com.foodcourt.user.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodcourt.user.domain.exception.CustomerRoleRequired;
import com.foodcourt.user.domain.exception.EmailAlreadyRegistered;
import com.foodcourt.user.domain.exception.IdentityDocumentAlreadyRegistered;
import com.foodcourt.user.domain.exception.InvalidPersonName;
import com.foodcourt.user.domain.exception.PasswordTooLong;
import com.foodcourt.user.domain.model.NewCustomer;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.PasswordHasher;
import com.foodcourt.user.domain.spi.UserRepository;

@ExtendWith(MockitoExtension.class)
class CreateCustomerUseCaseTest {

    private static final String RAW_PASSWORD = "secret12";
    private static final String PASSWORD_HASH = "hashed-secret12";

    @Mock
    private UserRepository users;

    @Mock
    private PasswordHasher passwords;

    private CreateCustomerUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateCustomerUseCase(users, passwords);
    }

    @Test
    void createStoresACustomerWithNoRestaurantAndNoBirthDate() {
        when(passwords.hash(RAW_PASSWORD)).thenReturn(PASSWORD_HASH);
        when(users.save(any(User.class))).thenAnswer(invocation -> withId(invocation.getArgument(0)));

        User created = useCase.create(customer("Ana", "Gómez", "Ana@Example.com", RAW_PASSWORD, Role.CUSTOMER.id()));

        ArgumentCaptor<User> stored = ArgumentCaptor.forClass(User.class);
        verify(users).save(stored.capture());
        verify(users).existsByEmail("ana@example.com");
        verify(users).existsByIdentityDocument("001098765432");
        assertThat(stored.getValue().id()).isNull();
        assertThat(stored.getValue().role()).isEqualTo(Role.CUSTOMER);
        assertThat(stored.getValue().restaurantId()).isNull();
        assertThat(stored.getValue().birthDate()).isNull();
        assertThat(stored.getValue().email()).isEqualTo("ana@example.com");
        assertThat(stored.getValue().identityDocument()).isEqualTo("001098765432");
        assertThat(stored.getValue().phone()).isEqualTo("+573001112233");
        assertThat(stored.getValue().passwordHash()).isEqualTo(PASSWORD_HASH);
        assertThat(stored.getValue().passwordHash()).isNotEqualTo(RAW_PASSWORD);
        assertThat(created.id()).isEqualTo(31L);
        assertThat(created.role()).isEqualTo(Role.CUSTOMER);
    }

    @Test
    void createCollapsesSpacesInTheStoredName() {
        when(passwords.hash(RAW_PASSWORD)).thenReturn(PASSWORD_HASH);
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = useCase.create(
                customer("  Ana   María  ", "  Gómez   2 ", "ana@example.com", RAW_PASSWORD, Role.CUSTOMER.id()));

        assertThat(created.firstName()).isEqualTo("Ana María");
        assertThat(created.lastName()).isEqualTo("Gómez 2");
    }

    @ParameterizedTest
    @ValueSource(shorts = {1, 2, 3, 99})
    void createRejectsARoleOtherThanCustomer(short roleId) {
        NewCustomer wrongRole = customer("Ana", "Gómez", "ana@example.com", RAW_PASSWORD, roleId);

        assertThatThrownBy(() -> useCase.create(wrongRole))
                .isInstanceOf(CustomerRoleRequired.class)
                .hasMessage("The role must be CUSTOMER");
        verifyNoInteractions(passwords, users);
    }

    @Test
    void createRejectsAMissingRole() {
        NewCustomer noRole = customer("Ana", "Gómez", "ana@example.com", RAW_PASSWORD, null);

        assertThatThrownBy(() -> useCase.create(noRole))
                .isInstanceOf(CustomerRoleRequired.class);
        verifyNoInteractions(passwords, users);
    }

    @Test
    void createRejectsAFirstNameShorterThanTwoAfterCollapsingSpaces() {
        NewCustomer shortName = customer("  A  ", "Gómez", "ana@example.com", RAW_PASSWORD, Role.CUSTOMER.id());

        assertThatThrownBy(() -> useCase.create(shortName))
                .isInstanceOf(InvalidPersonName.class);
        verifyNoInteractions(passwords, users);
    }

    @Test
    void createRejectsALastNameWithoutALetter() {
        NewCustomer digitsOnly = customer("Ana", "123", "ana@example.com", RAW_PASSWORD, Role.CUSTOMER.id());

        assertThatThrownBy(() -> useCase.create(digitsOnly))
                .isInstanceOf(InvalidPersonName.class);
        verifyNoInteractions(passwords, users);
    }

    @Test
    void createAcceptsAPasswordOfExactly72Bytes() {
        String password = "ñ".repeat(36);
        when(passwords.hash(password)).thenReturn(PASSWORD_HASH);
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = useCase.create(customer("Ana", "Gómez", "ana@example.com", password, Role.CUSTOMER.id()));

        assertThat(created.passwordHash()).isEqualTo(PASSWORD_HASH);
        verify(passwords).hash(password);
    }

    @Test
    void createRejectsAPasswordLongerThan72Bytes() {
        NewCustomer tooLong = customer("Ana", "Gómez", "ana@example.com", "ñ".repeat(37), Role.CUSTOMER.id());

        assertThatThrownBy(() -> useCase.create(tooLong))
                .isInstanceOf(PasswordTooLong.class)
                .hasMessage("The password must not exceed 72 bytes");
        verifyNoInteractions(passwords, users);
    }

    @Test
    void createRejectsADuplicateEmail() {
        when(users.existsByEmail("ana@example.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase.create(customer("Ana", "Gómez", "ANA@example.com", RAW_PASSWORD, Role.CUSTOMER.id())))
                .isInstanceOf(EmailAlreadyRegistered.class);
        verify(users, never()).existsByIdentityDocument(any());
        verify(users, never()).save(any());
        verifyNoInteractions(passwords);
    }

    @Test
    void createRejectsADuplicateIdentityDocument() {
        when(users.existsByIdentityDocument("001098765432")).thenReturn(true);

        assertThatThrownBy(() -> useCase.create(validCustomer()))
                .isInstanceOf(IdentityDocumentAlreadyRegistered.class);
        verify(users, never()).save(any());
        verifyNoInteractions(passwords);
    }

    @Test
    void createPropagatesADuplicateDetectedAtInsert() {
        when(passwords.hash(RAW_PASSWORD)).thenReturn(PASSWORD_HASH);
        when(users.save(any(User.class))).thenThrow(new EmailAlreadyRegistered());

        assertThatThrownBy(() -> useCase.create(validCustomer()))
                .isInstanceOf(EmailAlreadyRegistered.class);
    }

    private static NewCustomer validCustomer() {
        return customer("Ana", "Gómez", "ana@example.com", RAW_PASSWORD, Role.CUSTOMER.id());
    }

    private static NewCustomer customer(
            String firstName,
            String lastName,
            String email,
            String password,
            Short roleId) {
        return new NewCustomer(
                firstName,
                lastName,
                "001098765432",
                "+573001112233",
                email,
                password,
                roleId);
    }

    private static User withId(User user) {
        return new User(
                31L,
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
