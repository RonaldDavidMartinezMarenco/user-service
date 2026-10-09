package com.foodcourt.user.domain.spi;

/**
 * One-way password hash. The domain does not import a crypto library;
 * the adapter uses bcrypt.
 */
public interface PasswordHasher {

    String hash(String rawPassword);
}
