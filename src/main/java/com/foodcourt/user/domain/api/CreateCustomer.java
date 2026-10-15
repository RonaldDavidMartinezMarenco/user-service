package com.foodcourt.user.domain.api;

import com.foodcourt.user.domain.model.NewCustomer;
import com.foodcourt.user.domain.model.User;

/** Inbound port for registering a customer account. The call is public, so there is no caller. */
public interface CreateCustomer {

    User create(NewCustomer customer);
}
