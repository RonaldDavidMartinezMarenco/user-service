package com.foodcourt.user.infrastructure.input.rest.mapper;

import com.foodcourt.user.domain.model.NewOwner;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.infrastructure.input.rest.dto.CreateOwnerRequest;
import com.foodcourt.user.infrastructure.input.rest.dto.OwnerResponse;

/** Copies fields between the HTTP contract and the domain. Applies no rule. */
public final class OwnerRestMapper {

    public NewOwner toNewOwner(CreateOwnerRequest request) {
        return new NewOwner(
                request.firstName(),
                request.lastName(),
                request.identityDocument(),
                request.phone(),
                request.birthDate(),
                request.email(),
                request.password());
    }

    public OwnerResponse toResponse(User user) {
        return new OwnerResponse(
                user.id(),
                user.firstName(),
                user.lastName(),
                user.identityDocument(),
                user.phone(),
                user.birthDate(),
                user.email(),
                user.role());
    }
}
