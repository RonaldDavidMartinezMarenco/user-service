package com.foodcourt.user.application.handler;

import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.api.FindAccount;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.CurrentCaller;

/** Entry the controller calls. This type reads the caller and forwards the call. */
@Service
public final class FindAccountHandler {

    private final FindAccount findAccount;
    private final CurrentCaller currentCaller;

    public FindAccountHandler(FindAccount findAccount, CurrentCaller currentCaller) {
        this.findAccount = findAccount;
        this.currentCaller = currentCaller;
    }

    public User find(long id) {
        return findAccount.find(currentCaller.require(), id);
    }
}
