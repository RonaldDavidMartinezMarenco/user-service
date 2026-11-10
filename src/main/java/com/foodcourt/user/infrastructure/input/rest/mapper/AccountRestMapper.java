package com.foodcourt.user.infrastructure.input.rest.mapper;

import com.foodcourt.user.domain.model.CustomerPhone;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.infrastructure.input.rest.dto.AccountResponse;
import com.foodcourt.user.infrastructure.input.rest.dto.CustomerPhoneResponse;

/** Copies the id, the role, and the employee restaurant. Applies no rule and omits the password hash. */
public final class AccountRestMapper {

    public AccountResponse toResponse(User user) {
        return new AccountResponse(user.id(), user.role(), user.restaurantId());
    }

    public CustomerPhoneResponse toPhone(CustomerPhone phone) {
        return new CustomerPhoneResponse(phone.phone());
    }
}
