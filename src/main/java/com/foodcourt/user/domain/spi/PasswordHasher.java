package com.foodcourt.user.domain.spi;

/**
 * One-way password hash. The domain does not import a crypto library;
 * the adapter uses bcrypt.
 */
public interface PasswordHasher {

    String hash(String rawPassword);

    boolean matches(String rawPassword, String passwordHash);

    /**
     * Runs a bcrypt comparison against a fixed hash when the email is unknown,
     * so that path takes about as long as a real comparison.
     */
    void matchesUnknown(String rawPassword);
}
