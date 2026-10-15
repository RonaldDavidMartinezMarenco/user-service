package com.foodcourt.user.application.handler;

import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.api.CreateCustomer;
import com.foodcourt.user.domain.model.NewCustomer;
import com.foodcourt.user.domain.model.User;

/**
 * Entry the controller calls. Validation stays on the request DTO and the
 * rules stay in {@link CreateCustomer}. Registration is public, so this type reads no caller.
 */
@Service
public final class CreateCustomerHandler {

    private final CreateCustomer createCustomer;

    public CreateCustomerHandler(CreateCustomer createCustomer) {
        this.createCustomer = createCustomer;
    }

    public User create(NewCustomer customer) {
        return createCustomer.create(customer);
    }
}
