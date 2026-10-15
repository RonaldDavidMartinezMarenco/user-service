package com.foodcourt.user.application.usecase;

import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.api.CreateCustomer;
import com.foodcourt.user.domain.exception.CustomerRoleRequired;
import com.foodcourt.user.domain.exception.EmailAlreadyRegistered;
import com.foodcourt.user.domain.exception.IdentityDocumentAlreadyRegistered;
import com.foodcourt.user.domain.model.EmailAddress;
import com.foodcourt.user.domain.model.NewCustomer;
import com.foodcourt.user.domain.model.PasswordPolicy;
import com.foodcourt.user.domain.model.PersonName;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.PasswordHasher;
import com.foodcourt.user.domain.spi.UserRepository;

/**
 * Registers a customer account. Checks run in this order, and a rejection does not save:
 * the requested role is {@link Role#CUSTOMER}, first name, last name, a password bcrypt can store,
 * unique email, unique identity document, then bcrypt, then insert.
 * {@code birthDate} and {@code restaurantId} stay null.
 */
@Service
public final class CreateCustomerUseCase implements CreateCustomer {

    private final UserRepository users;
    private final PasswordHasher passwords;

    public CreateCustomerUseCase(UserRepository users, PasswordHasher passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    @Override
    public User create(NewCustomer customer) {
        requireCustomerRole(customer);
        PersonName firstName = new PersonName(customer.firstName());
        PersonName lastName = new PersonName(customer.lastName());
        PasswordPolicy.requireStorable(customer.password());
        String email = new EmailAddress(customer.email()).value();
        rejectDuplicateEmail(email);
        rejectDuplicateDocument(customer.identityDocument());
        String passwordHash = passwords.hash(customer.password());
        return users.save(account(customer, firstName, lastName, email, passwordHash));
    }

    private static void requireCustomerRole(NewCustomer customer) {
        if (!Short.valueOf(Role.CUSTOMER.id()).equals(customer.roleId())) {
            throw new CustomerRoleRequired();
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
            NewCustomer customer,
            PersonName firstName,
            PersonName lastName,
            String email,
            String passwordHash) {
        return new User(
                null,
                firstName.value(),
                lastName.value(),
                customer.identityDocument(),
                customer.phone(),
                null,
                email,
                passwordHash,
                Role.CUSTOMER,
                null);
    }
}
