package com.foodcourt.user.infrastructure.security;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.spi.PasswordHasher;

/** Bcrypt adapter. The hash cannot be read back to the raw password. */
@Service
public final class BCryptPasswordHasher implements PasswordHasher {

    private final PasswordEncoder passwordEncoder;
    private final String unknownHash;

    public BCryptPasswordHasher(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
        this.unknownHash = passwordEncoder.encode("unknown-account");
    }

    @Override
    public String hash(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String passwordHash) {
        return passwordEncoder.matches(rawPassword, passwordHash);
    }

    @Override
    public void matchesUnknown(String rawPassword) {
        passwordEncoder.matches(rawPassword, unknownHash);
    }
}
