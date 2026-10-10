package com.foodcourt.user.domain.api;

import com.foodcourt.user.domain.model.Caller;
import com.foodcourt.user.domain.model.User;

/**
 * Inbound port for reading an account by id.
 * An administrator may read any id. Any other caller may read only their own.
 */
public interface FindAccount {

    User find(Caller caller, long id);
}
