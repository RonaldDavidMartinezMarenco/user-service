package com.foodcourt.user.domain.spi;

import com.foodcourt.user.domain.model.User;

/**
 * Persistence port for {@code app_user}.
 * Lookups use the normalized email and the identity document as stored.
 */
public interface UserRepository {

    boolean existsByEmail(String email);

    boolean existsByIdentityDocument(String identityDocument);

    User save(User user);
}
