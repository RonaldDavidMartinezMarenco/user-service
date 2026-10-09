package com.foodcourt.user.domain.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EmailAddressTest {

    @Test
    void storesTheMailboxInLowerCase() {
        assertThat(new EmailAddress("Ana@Example.com").value()).isEqualTo("ana@example.com");
    }
}
