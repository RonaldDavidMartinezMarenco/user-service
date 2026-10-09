package com.foodcourt.user.infrastructure.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

class BCryptPasswordHasherTest {

    @Test
    void hashIsBcryptAndDoesNotKeepTheRawPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        BCryptPasswordHasher hasher = new BCryptPasswordHasher(encoder);

        String hash = hasher.hash("secret12");

        assertThat(hash).isNotEqualTo("secret12");
        assertThat(encoder.matches("secret12", hash)).isTrue();
    }
}
