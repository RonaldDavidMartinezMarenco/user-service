package com.foodcourt.user.application.usecase;

import java.time.Clock;
import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.api.CreateOwner;
import com.foodcourt.user.domain.exception.EmailAlreadyRegistered;
import com.foodcourt.user.domain.exception.IdentityDocumentAlreadyRegistered;
import com.foodcourt.user.domain.model.EmailAddress;
import com.foodcourt.user.domain.model.NewOwner;
import com.foodcourt.user.domain.model.OwnerAge;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.PasswordHasher;
import com.foodcourt.user.domain.spi.UserRepository;

/**
 * Creates an owner account. Checks run in this order, and a rejection does not save:
 * adult, unique email, unique identity document, then bcrypt, then insert.
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
    public User create(NewOwner owner) {
        requireAdult(owner);
        rejectDuplicateEmail(owner);
        rejectDuplicateDocument(owner);
        return users.save(account(owner, passwords.hash(owner.password())));
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

    private static User account(NewOwner owner, String passwordHash) {
        return new User(
                null,
                owner.firstName(),
                owner.lastName(),
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
