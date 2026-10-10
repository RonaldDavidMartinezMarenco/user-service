package com.foodcourt.user.application.usecase;

import java.time.Clock;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.api.CreateOwner;
import com.foodcourt.user.domain.exception.AccessDenied;
import com.foodcourt.user.domain.exception.EmailAlreadyRegistered;
import com.foodcourt.user.domain.exception.IdentityDocumentAlreadyRegistered;
import com.foodcourt.user.domain.model.Caller;
import com.foodcourt.user.domain.model.EmailAddress;
import com.foodcourt.user.domain.model.NewOwner;
import com.foodcourt.user.domain.model.OwnerAge;
import com.foodcourt.user.domain.model.PersonName;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.PasswordHasher;
import com.foodcourt.user.domain.spi.UserRepository;

/**
 * Creates an owner account. Checks run in this order, and a rejection does not save:
 * the caller is an administrator, first name, last name, adult, unique email,
 * unique identity document, then bcrypt, then insert.
 * The role is always {@link Role#OWNER} and {@code restaurantId} stays null.
 */
@Service
public final class CreateOwnerUseCase implements CreateOwner {

    private final UserRepository users;
    private final PasswordHasher passwords;
    private final Clock clock;

    public CreateOwnerUseCase(UserRepository users, PasswordHasher passwords, Clock clock) {
        this.users = users;
        this.passwords = passwords;
        this.clock = clock;
    }

    @Override
    public User create(Caller caller, NewOwner owner) {
        requireAdministrator(caller);
        PersonName firstName = requireName(owner.firstName());
        PersonName lastName = requireName(owner.lastName());
        requireAdult(owner);
        rejectDuplicateEmail(owner);
        rejectDuplicateDocument(owner);
        return users.save(account(owner, firstName, lastName, passwords.hash(owner.password())));
    }

    private static void requireAdministrator(Caller caller) {
        if (caller.role() != Role.ADMINISTRATOR) {
            throw new AccessDenied();
        }
    }

    private static PersonName requireName(String name) {
        return new PersonName(name);
    }

    private void requireAdult(NewOwner owner) {
        OwnerAge.requireAdult(owner.birthDate(), LocalDate.now(clock));
    }

    private void rejectDuplicateEmail(NewOwner owner) {
        if (users.existsByEmail(normalizedEmail(owner))) {
            throw new EmailAlreadyRegistered();
        }
    }

    private void rejectDuplicateDocument(NewOwner owner) {
        if (users.existsByIdentityDocument(owner.identityDocument())) {
            throw new IdentityDocumentAlreadyRegistered();
        }
    }

    private static User account(NewOwner owner, PersonName firstName, PersonName lastName, String passwordHash) {
        return new User(
                null,
                firstName.value(),
                lastName.value(),
                owner.identityDocument(),
                owner.phone(),
                owner.birthDate(),
                normalizedEmail(owner),
                passwordHash,
                Role.OWNER,
                null);
    }

    private static String normalizedEmail(NewOwner owner) {
        return new EmailAddress(owner.email()).value();
    }
}
