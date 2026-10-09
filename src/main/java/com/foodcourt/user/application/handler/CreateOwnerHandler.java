package com.foodcourt.user.application.handler;

import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.api.CreateOwner;
import com.foodcourt.user.domain.model.NewOwner;
import com.foodcourt.user.domain.model.User;

/**
 * Entry the controller calls. Validation stays on the request DTO and the
 * rules stay in {@link CreateOwner}; this type only forwards the call.
 */
@Service
public final class CreateOwnerHandler {

    private final CreateOwner createOwner;

    public CreateOwnerHandler(CreateOwner createOwner) {
        this.createOwner = createOwner;
    }

    public User create(NewOwner owner) {
        return createOwner.create(owner);
    }
}
