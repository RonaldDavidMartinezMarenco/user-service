package com.foodcourt.user.domain.spi;

import com.foodcourt.user.domain.model.AccessToken;
import com.foodcourt.user.domain.model.User;

/** Signs an access token for an account that has already been authenticated. */
public interface AccessTokenIssuer {

    AccessToken issue(User user);
}
