package com.foodcourt.user.domain.api;

import com.foodcourt.user.domain.model.NewOwner;
import com.foodcourt.user.domain.model.User;

/** Inbound port for creating an owner account. */
public interface CreateOwner {

    User create(NewOwner owner);
}
