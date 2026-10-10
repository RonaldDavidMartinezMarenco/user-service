package com.foodcourt.user.application.usecase;

import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.api.FindAccount;
import com.foodcourt.user.domain.exception.AccessDenied;
import com.foodcourt.user.domain.exception.UserNotFound;
import com.foodcourt.user.domain.model.Caller;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.UserRepository;

/**
 * Reads an account by id. An administrator may read any id. Any other caller
 * may read only their own. A forbidden caller is rejected before the lookup,
 * so a missing row is not revealed. A missing row for an allowed caller is {@link UserNotFound}.
 */
@Service
public final class FindAccountUseCase implements FindAccount {

    private final UserRepository users;

    public FindAccountUseCase(UserRepository users) {
        this.users = users;
    }

    @Override
    public User find(Caller caller, long id) {
        requireAllowed(caller, id);
        return users.findById(id).orElseThrow(UserNotFound::new);
    }

    private static void requireAllowed(Caller caller, long id) {
        if (!allowed(caller, id)) {
            throw new AccessDenied();
        }
    }

    private static boolean allowed(Caller caller, long id) {
        return caller.role() == Role.ADMINISTRATOR || caller.id() == id;
    }
}
