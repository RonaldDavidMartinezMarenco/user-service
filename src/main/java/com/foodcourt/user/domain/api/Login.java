package com.foodcourt.user.domain.api;

import com.foodcourt.user.domain.model.AccessToken;
import com.foodcourt.user.domain.model.Credentials;

/** Inbound port for exchanging email and password for an access token. */
public interface Login {

    AccessToken login(Credentials credentials);
}
