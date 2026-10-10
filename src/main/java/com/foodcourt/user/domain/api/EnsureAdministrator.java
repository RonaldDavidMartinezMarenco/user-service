package com.foodcourt.user.domain.api;

import com.foodcourt.user.domain.model.NewAdministrator;

/**
 * Inbound port that inserts the first administrator once.
 * A later start does not change that account.
 */
public interface EnsureAdministrator {

    void ensure(NewAdministrator administrator);
}
