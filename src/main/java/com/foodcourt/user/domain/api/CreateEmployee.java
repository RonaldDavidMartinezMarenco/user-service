package com.foodcourt.user.domain.api;

import com.foodcourt.user.domain.model.Caller;
import com.foodcourt.user.domain.model.NewEmployee;
import com.foodcourt.user.domain.model.User;

/** Inbound port for creating an employee account. The caller must own the restaurant. */
public interface CreateEmployee {

    User create(Caller caller, NewEmployee employee);
}
