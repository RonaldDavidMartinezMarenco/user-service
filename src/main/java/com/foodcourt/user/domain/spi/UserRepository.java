package com.foodcourt.user.domain.spi;

import java.util.Optional;

import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;

/**
 * Persistence port for {@code app_user}.
 * Lookups use the normalized email and the identity document as stored.
 */
public interface UserRepository {

    Optional<User> findById(long id);

    Optional<User> findByEmail(String email);

    boolean existsByRole(Role role);

    boolean existsByEmail(String email);

    boolean existsByIdentityDocument(String identityDocument);

    User save(User user);
}
