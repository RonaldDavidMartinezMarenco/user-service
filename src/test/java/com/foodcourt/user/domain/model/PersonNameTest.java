package com.foodcourt.user.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.foodcourt.user.domain.exception.InvalidPersonName;

class PersonNameTest {

    @Test
    void collapsesInternalSpaces() {
        assertThat(new PersonName("  María   López  ").value()).isEqualTo("María López");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Ana", "La 14", "O'Brien", "Café & Bar", "Ana2"})
    void acceptsANameWithALetter(String name) {
        assertThat(new PersonName(name).value()).isEqualTo(name);
    }

    @ParameterizedTest
    @ValueSource(strings = {"A", "123", "***", "Ana!", "  A  "})
    void rejectsANameOutsideTheShape(String name) {
        assertThatThrownBy(() -> new PersonName(name)).isInstanceOf(InvalidPersonName.class);
    }

    @Test
    void rejectsNull() {
        assertThatThrownBy(() -> new PersonName(null)).isInstanceOf(InvalidPersonName.class);
    }

    @Test
    void rejectsANameLongerThanTheMaximum() {
        assertThatThrownBy(() -> new PersonName("A" + "a".repeat(PersonName.MAX)))
                .isInstanceOf(InvalidPersonName.class);
    }
}
