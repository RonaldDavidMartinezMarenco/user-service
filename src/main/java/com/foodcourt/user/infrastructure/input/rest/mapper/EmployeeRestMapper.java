package com.foodcourt.user.infrastructure.input.rest.mapper;

import com.foodcourt.user.domain.model.NewEmployee;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.infrastructure.input.rest.dto.CreateEmployeeRequest;
import com.foodcourt.user.infrastructure.input.rest.dto.EmployeeResponse;

/** Copies fields between the HTTP contract and the domain. Applies no rule. */
public final class EmployeeRestMapper {

    public NewEmployee toNewEmployee(CreateEmployeeRequest request) {
        return new NewEmployee(
                request.firstName(),
                request.lastName(),
                request.identityDocument(),
                request.phone(),
                request.email(),
                request.password(),
                request.roleId(),
                request.restaurantId());
    }

    public EmployeeResponse toResponse(User user) {
        return new EmployeeResponse(
                user.id(),
                user.firstName(),
                user.lastName(),
                user.identityDocument(),
                user.phone(),
                user.email(),
                user.role(),
                user.restaurantId());
    }
}
