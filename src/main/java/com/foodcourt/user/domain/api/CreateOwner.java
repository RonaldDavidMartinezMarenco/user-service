package com.foodcourt.user.domain.api;

import com.foodcourt.user.domain.model.Caller;
import com.foodcourt.user.domain.model.NewOwner;
import com.foodcourt.user.domain.model.User;

/** Inbound port for creating an owner account. The caller must be an administrator. */
public interface CreateOwner {

    User create(Caller caller, NewOwner owner);
}
