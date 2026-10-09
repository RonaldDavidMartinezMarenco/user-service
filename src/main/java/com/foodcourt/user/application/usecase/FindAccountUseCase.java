package com.foodcourt.user.application.usecase;

import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.api.FindAccount;
import com.foodcourt.user.domain.exception.UserNotFound;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.UserRepository;

/**
 * Reads an account by id. A missing row is {@link UserNotFound}.
 * The role is returned as stored; callers decide whether that role is allowed.
 */
@Service
public final class FindAccountUseCase implements FindAccount {

    private final UserRepository users;

    public FindAccountUseCase(UserRepository users) {
        this.users = users;
    }

    @Override
    public User find(long id) {
        return users.findById(id).orElseThrow(UserNotFound::new);
    }
}
