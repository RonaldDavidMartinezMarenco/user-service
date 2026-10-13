package com.foodcourt.user.application.usecase;

import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.api.CreateEmployee;
import com.foodcourt.user.domain.exception.AccessDenied;
import com.foodcourt.user.domain.exception.EmailAlreadyRegistered;
import com.foodcourt.user.domain.exception.EmployeeRoleRequired;
import com.foodcourt.user.domain.exception.IdentityDocumentAlreadyRegistered;
import com.foodcourt.user.domain.exception.RestaurantNotFound;
import com.foodcourt.user.domain.model.Caller;
import com.foodcourt.user.domain.model.EmailAddress;
import com.foodcourt.user.domain.model.NewEmployee;
import com.foodcourt.user.domain.model.PersonName;
import com.foodcourt.user.domain.model.RestaurantOwnership;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.PasswordHasher;
import com.foodcourt.user.domain.spi.RestaurantDirectory;
import com.foodcourt.user.domain.spi.UserRepository;

/**
 * Creates an employee account. Checks run in this order, and a rejection does not save:
 * the caller is an owner, the requested role is {@link Role#EMPLOYEE}, first name, last name,
 * the restaurant exists, the caller owns it, unique email, unique identity document,
 * then bcrypt, then insert. {@code birthDate} stays null.
 */
@Service
public final class CreateEmployeeUseCase implements CreateEmployee {

    private final UserRepository users;
    private final PasswordHasher passwords;
    private final RestaurantDirectory restaurants;

    public CreateEmployeeUseCase(UserRepository users, PasswordHasher passwords, RestaurantDirectory restaurants) {
        this.users = users;
        this.passwords = passwords;
        this.restaurants = restaurants;
    }

    @Override
    public User create(Caller caller, NewEmployee employee) {
        requireOwner(caller);
        requireEmployeeRole(employee);
        PersonName firstName = new PersonName(employee.firstName());
        PersonName lastName = new PersonName(employee.lastName());
        RestaurantOwnership restaurant = requireRestaurant(employee.restaurantId());
        requireOwnsRestaurant(caller, restaurant);
        String email = new EmailAddress(employee.email()).value();
        rejectDuplicateEmail(email);
        rejectDuplicateDocument(employee.identityDocument());
        String passwordHash = passwords.hash(employee.password());
        return users.save(account(employee, firstName, lastName, email, passwordHash, restaurant));
    }

    private static void requireOwner(Caller caller) {
        if (caller.role() != Role.OWNER) {
            throw new AccessDenied();
        }
    }

    private static void requireEmployeeRole(NewEmployee employee) {
        if (!Short.valueOf(Role.EMPLOYEE.id()).equals(employee.roleId())) {
            throw new EmployeeRoleRequired();
        }
    }

    private RestaurantOwnership requireRestaurant(long restaurantId) {
        return restaurants.findById(restaurantId).orElseThrow(RestaurantNotFound::new);
    }

    private static void requireOwnsRestaurant(Caller caller, RestaurantOwnership restaurant) {
        if (restaurant.ownerId() != caller.id()) {
            throw new AccessDenied();
        }
    }

    private void rejectDuplicateEmail(String email) {
        if (users.existsByEmail(email)) {
            throw new EmailAlreadyRegistered();
        }
    }

    private void rejectDuplicateDocument(String identityDocument) {
        if (users.existsByIdentityDocument(identityDocument)) {
            throw new IdentityDocumentAlreadyRegistered();
        }
    }

    private static User account(
            NewEmployee employee,
            PersonName firstName,
            PersonName lastName,
            String email,
            String passwordHash,
            RestaurantOwnership restaurant) {
        return new User(
                null,
                firstName.value(),
                lastName.value(),
                employee.identityDocument(),
                employee.phone(),
                null,
                email,
                passwordHash,
                Role.EMPLOYEE,
                restaurant.id());
    }
}
