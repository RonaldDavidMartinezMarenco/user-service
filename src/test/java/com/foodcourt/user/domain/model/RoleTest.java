package com.foodcourt.user.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class RoleTest {

    @Test
    void ownerIdMatchesTheRoleTable() {
        assertThat(Role.OWNER.id()).isEqualTo((short) 2);
        assertThat(Role.fromId((short) 2)).isEqualTo(Role.OWNER);
    }

    @Test
    void fromIdRejectsAnUnknownRole() {
        assertThatThrownBy(() -> Role.fromId((short) 9))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Unknown role id: 9");
    }
}
