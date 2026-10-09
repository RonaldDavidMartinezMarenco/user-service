package com.foodcourt.user.infrastructure.input.rest.mapper;

import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.infrastructure.input.rest.dto.AccountResponse;

/** Copies the id and the role. Applies no rule and omits the password hash. */
public final class AccountRestMapper {

    public AccountResponse toResponse(User user) {
        return new AccountResponse(user.id(), user.role());
    }
}
