package com.foodcourt.user.application.usecase;

import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.api.EnsureAdministrator;
import com.foodcourt.user.domain.exception.InvalidAdministratorAccount;
import com.foodcourt.user.domain.model.EmailAddress;
import com.foodcourt.user.domain.model.NewAdministrator;
import com.foodcourt.user.domain.model.PersonName;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.PasswordHasher;
import com.foodcourt.user.domain.spi.UserRepository;

/**
 * Inserts the first administrator. Checks run in this order, and a rejection does not save:
 * name, password length, document, phone, email, then whether an administrator already exists.
 * An existing administrator is left unchanged, including its password.
 */
@Service
public final class EnsureAdministratorUseCase implements EnsureAdministrator {

    private static final int PASSWORD_MIN = 8;
    private static final int PASSWORD_MAX = 72;
    private static final int PHONE_MAX = 13;
    private static final Pattern DOCUMENT = Pattern.compile("^[0-9]+$");
    private static final Pattern PHONE = Pattern.compile("^\\+?[0-9]+$");

    private final UserRepository users;
    private final PasswordHasher passwords;

    public EnsureAdministratorUseCase(UserRepository users, PasswordHasher passwords) {
        this.users = users;
        this.passwords = passwords;
    }

    @Override
    public void ensure(NewAdministrator draft) {
        User account = account(draft);
        if (users.existsByRole(Role.ADMINISTRATOR)) {
            return;
        }
        rejectDuplicates(account);
        users.save(withHash(account, draft.password()));
    }

    private User account(NewAdministrator draft) {
        PersonName firstName = new PersonName(draft.firstName());
        PersonName lastName = new PersonName(draft.lastName());
        requirePassword(draft.password());
        return new User(
                null,
                firstName.value(),
                lastName.value(),
                requireDocument(draft.identityDocument()),
                requirePhone(draft.phone()),
                null,
                requireEmail(draft.email()),
                null,
                Role.ADMINISTRATOR,
                null);
    }

    private void rejectDuplicates(User account) {
        if (users.existsByEmail(account.email())) {
            throw new InvalidAdministratorAccount("Email is already registered");
        }
        rejectDuplicateDocument(account.identityDocument());
    }

    private void rejectDuplicateDocument(String identityDocument) {
        if (users.existsByIdentityDocument(identityDocument)) {
            throw new InvalidAdministratorAccount("Identity document is already registered");
        }
    }

    private User withHash(User account, String rawPassword) {
        return new User(
                account.id(),
                account.firstName(),
                account.lastName(),
                account.identityDocument(),
                account.phone(),
                account.birthDate(),
                account.email(),
                passwords.hash(rawPassword),
                account.role(),
                account.restaurantId());
    }

    private static void requirePassword(String password) {
        if (!passwordLength(password)) {
            throw new InvalidAdministratorAccount("The administrator password must contain 8 to 72 characters");
        }
    }

    private static boolean passwordLength(String password) {
        return password != null && password.length() >= PASSWORD_MIN && password.length() <= PASSWORD_MAX;
    }

    private static String requireDocument(String identityDocument) {
        if (!documentShape(identityDocument)) {
            throw new InvalidAdministratorAccount("The administrator identity document must be numeric");
        }
        return identityDocument;
    }

    private static boolean documentShape(String identityDocument) {
        return identityDocument != null && DOCUMENT.matcher(identityDocument).matches();
    }

    private static String requirePhone(String phone) {
        if (!phoneShape(phone)) {
            throw new InvalidAdministratorAccount("The administrator phone must contain at most 13 digits and may start with +");
        }
        return phone;
    }

    private static boolean phoneShape(String phone) {
        return phone != null && phone.length() <= PHONE_MAX && PHONE.matcher(phone).matches();
    }

    private static String requireEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new InvalidAdministratorAccount("The administrator email is required");
        }
        return new EmailAddress(email).value();
    }
}
