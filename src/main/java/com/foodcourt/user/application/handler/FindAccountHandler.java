package com.foodcourt.user.application.handler;

import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.api.FindAccount;
import com.foodcourt.user.domain.model.User;

/** Entry the controller calls. This type only forwards the call. */
@Service
public final class FindAccountHandler {

    private final FindAccount findAccount;

    public FindAccountHandler(FindAccount findAccount) {
        this.findAccount = findAccount;
    }

    public User find(long id) {
        return findAccount.find(id);
    }
}
