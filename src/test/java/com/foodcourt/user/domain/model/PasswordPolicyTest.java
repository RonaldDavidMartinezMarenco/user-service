package com.foodcourt.user.domain.model;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import com.foodcourt.user.domain.exception.PasswordTooLong;

class PasswordPolicyTest {

    @ParameterizedTest
    @MethodSource("storablePasswords")
    void acceptsAPasswordOfAtMost72Bytes(String password) {
        assertThatNoException().isThrownBy(() -> PasswordPolicy.requireStorable(password));
    }

    @ParameterizedTest
    @MethodSource("passwordsPastTheLimit")
    void rejectsAPasswordLongerThan72Bytes(String password) {
        assertThatThrownBy(() -> PasswordPolicy.requireStorable(password))
                .isInstanceOf(PasswordTooLong.class)
                .hasMessage("The password must not exceed 72 bytes");
    }

    private static String[] storablePasswords() {
        return new String[] {"a".repeat(72), "ñ".repeat(36)};
    }

    private static String[] passwordsPastTheLimit() {
        return new String[] {"a".repeat(73), "ñ".repeat(37)};
    }
}
