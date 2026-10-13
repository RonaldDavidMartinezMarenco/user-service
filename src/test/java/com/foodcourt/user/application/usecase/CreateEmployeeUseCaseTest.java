package com.foodcourt.user.application.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodcourt.user.domain.exception.AccessDenied;
import com.foodcourt.user.domain.exception.EmailAlreadyRegistered;
import com.foodcourt.user.domain.exception.EmployeeRoleRequired;
import com.foodcourt.user.domain.exception.IdentityDocumentAlreadyRegistered;
import com.foodcourt.user.domain.exception.InvalidPersonName;
import com.foodcourt.user.domain.exception.RestaurantDirectoryUnavailable;
import com.foodcourt.user.domain.exception.RestaurantNotFound;
import com.foodcourt.user.domain.model.Caller;
import com.foodcourt.user.domain.model.NewEmployee;
import com.foodcourt.user.domain.model.RestaurantOwnership;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.PasswordHasher;
import com.foodcourt.user.domain.spi.RestaurantDirectory;
import com.foodcourt.user.domain.spi.UserRepository;

@ExtendWith(MockitoExtension.class)
class CreateEmployeeUseCaseTest {

    private static final long OWNER_ID = 15L;
    private static final long RESTAURANT_ID = 8L;
    private static final Caller OWNER = new Caller(OWNER_ID, Role.OWNER);
    private static final String RAW_PASSWORD = "secret12";
    private static final String PASSWORD_HASH = "hashed-secret12";

    @Mock
    private UserRepository users;

    @Mock
    private PasswordHasher passwords;

    @Mock
    private RestaurantDirectory restaurants;

    private CreateEmployeeUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateEmployeeUseCase(users, passwords, restaurants);
    }

    @Test
    void createStoresAnEmployeeOfTheOwnersRestaurant() {
        ownedRestaurant();
        when(passwords.hash(RAW_PASSWORD)).thenReturn(PASSWORD_HASH);
        when(users.save(any(User.class))).thenAnswer(invocation -> withId(invocation.getArgument(0)));

        User created = useCase.create(OWNER, employee("Luis", "Pérez", "Luis@Example.com", Role.EMPLOYEE.id()));

        ArgumentCaptor<User> stored = ArgumentCaptor.forClass(User.class);
        verify(users).save(stored.capture());
        verify(users).existsByEmail("luis@example.com");
        verify(users).existsByIdentityDocument("0012345678");
        assertThat(stored.getValue().id()).isNull();
        assertThat(stored.getValue().role()).isEqualTo(Role.EMPLOYEE);
        assertThat(stored.getValue().restaurantId()).isEqualTo(RESTAURANT_ID);
        assertThat(stored.getValue().birthDate()).isNull();
        assertThat(stored.getValue().email()).isEqualTo("luis@example.com");
        assertThat(stored.getValue().identityDocument()).isEqualTo("0012345678");
        assertThat(stored.getValue().phone()).isEqualTo("+573005698325");
        assertThat(stored.getValue().passwordHash()).isEqualTo(PASSWORD_HASH);
        assertThat(stored.getValue().passwordHash()).isNotEqualTo(RAW_PASSWORD);
        assertThat(created.id()).isEqualTo(30L);
        assertThat(created.role()).isEqualTo(Role.EMPLOYEE);
    }

    @Test
    void createCollapsesSpacesInTheStoredName() {
        ownedRestaurant();
        when(passwords.hash(RAW_PASSWORD)).thenReturn(PASSWORD_HASH);
        when(users.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User created = useCase.create(OWNER, employee("  Luis   Ana  ", "  Pérez   2 ", "luis@example.com", Role.EMPLOYEE.id()));

        assertThat(created.firstName()).isEqualTo("Luis Ana");
        assertThat(created.lastName()).isEqualTo("Pérez 2");
    }

    @ParameterizedTest
    @EnumSource(value = Role.class, names = "OWNER", mode = EnumSource.Mode.EXCLUDE)
    void createRejectsACallerThatIsNotAnOwner(Role role) {
        Caller caller = new Caller(OWNER_ID, role);

        assertThatThrownBy(() -> useCase.create(caller, validEmployee()))
                .isInstanceOf(AccessDenied.class);
        verifyNoInteractions(restaurants, passwords, users);
    }

    @ParameterizedTest
    @ValueSource(shorts = {1, 2, 4, 99})
    void createRejectsARoleOtherThanEmployee(short roleId) {
        NewEmployee wrongRole = employee("Luis", "Pérez", "luis@example.com", roleId);

        assertThatThrownBy(() -> useCase.create(OWNER, wrongRole))
                .isInstanceOf(EmployeeRoleRequired.class)
                .hasMessage("The role must be EMPLOYEE");
        verifyNoInteractions(restaurants, passwords, users);
    }

    @Test
    void createRejectsAMissingRole() {
        NewEmployee noRole = employee("Luis", "Pérez", "luis@example.com", null);

        assertThatThrownBy(() -> useCase.create(OWNER, noRole))
                .isInstanceOf(EmployeeRoleRequired.class);
        verifyNoInteractions(restaurants, passwords, users);
    }

    @Test
    void createRejectsAFirstNameShorterThanTwoAfterCollapsingSpaces() {
        NewEmployee shortName = employee("  L  ", "Pérez", "luis@example.com", Role.EMPLOYEE.id());

        assertThatThrownBy(() -> useCase.create(OWNER, shortName))
                .isInstanceOf(InvalidPersonName.class);
        verifyNoInteractions(restaurants, passwords, users);
    }

    @Test
    void createRejectsALastNameWithoutALetter() {
        NewEmployee digitsOnly = employee("Luis", "123", "luis@example.com", Role.EMPLOYEE.id());

        assertThatThrownBy(() -> useCase.create(OWNER, digitsOnly))
                .isInstanceOf(InvalidPersonName.class);
        verifyNoInteractions(restaurants, passwords, users);
    }

    @Test
    void createRejectsAMissingRestaurant() {
        when(restaurants.findById(RESTAURANT_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.create(OWNER, validEmployee()))
                .isInstanceOf(RestaurantNotFound.class)
                .hasMessage("Restaurant not found");
        verifyNoInteractions(passwords, users);
    }

    @Test
    void createRejectsARestaurantOfAnotherOwner() {
        when(restaurants.findById(RESTAURANT_ID))
                .thenReturn(Optional.of(new RestaurantOwnership(RESTAURANT_ID, 99L)));

        assertThatThrownBy(() -> useCase.create(OWNER, validEmployee()))
                .isInstanceOf(AccessDenied.class);
        verifyNoInteractions(passwords, users);
    }

    @Test
    void createPropagatesAnUnavailablePlazaServiceWithoutSaving() {
        when(restaurants.findById(anyLong())).thenThrow(new RestaurantDirectoryUnavailable());

        assertThatThrownBy(() -> useCase.create(OWNER, validEmployee()))
                .isInstanceOf(RestaurantDirectoryUnavailable.class);
        verifyNoInteractions(passwords, users);
    }

    @Test
    void createRejectsADuplicateEmail() {
        ownedRestaurant();
        when(users.existsByEmail("luis@example.com")).thenReturn(true);

        assertThatThrownBy(() -> useCase.create(OWNER, employee("Luis", "Pérez", "LUIS@example.com", Role.EMPLOYEE.id())))
                .isInstanceOf(EmailAlreadyRegistered.class);
        verify(users, never()).existsByIdentityDocument(any());
        verify(users, never()).save(any());
        verifyNoInteractions(passwords);
    }

    @Test
    void createRejectsADuplicateIdentityDocument() {
        ownedRestaurant();
        when(users.existsByIdentityDocument("0012345678")).thenReturn(true);

        assertThatThrownBy(() -> useCase.create(OWNER, validEmployee()))
                .isInstanceOf(IdentityDocumentAlreadyRegistered.class);
        verify(users, never()).save(any());
        verifyNoInteractions(passwords);
    }

    private void ownedRestaurant() {
        when(restaurants.findById(RESTAURANT_ID))
                .thenReturn(Optional.of(new RestaurantOwnership(RESTAURANT_ID, OWNER_ID)));
    }

    private static NewEmployee validEmployee() {
        return employee("Luis", "Pérez", "luis@example.com", Role.EMPLOYEE.id());
    }

    private static NewEmployee employee(String firstName, String lastName, String email, Short roleId) {
        return new NewEmployee(
                firstName,
                lastName,
                "0012345678",
                "+573005698325",
                email,
                RAW_PASSWORD,
                roleId,
                RESTAURANT_ID);
    }

    private static User withId(User user) {
        return new User(
                30L,
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
