package com.foodcourt.user.infrastructure.input.rest.mapper;

import com.foodcourt.user.domain.model.NewCustomer;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.infrastructure.input.rest.dto.CreateCustomerRequest;
import com.foodcourt.user.infrastructure.input.rest.dto.CustomerResponse;

/** Copies fields between the HTTP contract and the domain. Applies no rule. */
public final class CustomerRestMapper {

    public NewCustomer toNewCustomer(CreateCustomerRequest request) {
        return new NewCustomer(
                request.firstName(),
                request.lastName(),
                request.identityDocument(),
                request.phone(),
                request.email(),
                request.password(),
                request.roleId());
    }

    public CustomerResponse toResponse(User user) {
        return new CustomerResponse(
                user.id(),
                user.firstName(),
                user.lastName(),
                user.identityDocument(),
                user.phone(),
                user.email(),
                user.role());
    }
}
