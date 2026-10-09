package com.foodcourt.user.domain.api;

import com.foodcourt.user.domain.model.User;

/** Inbound port for reading an account by id. Does not filter by role. */
public interface FindAccount {

    User find(long id);
}
