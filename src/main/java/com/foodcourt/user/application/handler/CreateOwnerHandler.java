package com.foodcourt.user.application.handler;

import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.api.CreateOwner;
import com.foodcourt.user.domain.model.NewOwner;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.CurrentCaller;

/**
 * Entry the controller calls. Validation stays on the request DTO and the
 * rules stay in {@link CreateOwner}. This type reads the caller and forwards the call.
 */
@Service
public final class CreateOwnerHandler {

    private final CreateOwner createOwner;
    private final CurrentCaller currentCaller;

    public CreateOwnerHandler(CreateOwner createOwner, CurrentCaller currentCaller) {
        this.createOwner = createOwner;
        this.currentCaller = currentCaller;
    }

    public User create(NewOwner owner) {
        return createOwner.create(currentCaller.require(), owner);
    }
}
