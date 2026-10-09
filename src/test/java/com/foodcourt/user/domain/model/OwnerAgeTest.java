package com.foodcourt.user.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.foodcourt.user.domain.exception.UnderageOwner;

class OwnerAgeTest {

    private final LocalDate today = LocalDate.of(2026, 10, 8);

    @Test
    void requireAdultAcceptsCompletedEighteenYears() {
        OwnerAge.requireAdult(LocalDate.of(2008, 10, 8), today);
    }

    @Test
    void requireAdultRejectsSeventeenCompletedYears() {
        assertThatThrownBy(() -> OwnerAge.requireAdult(LocalDate.of(2008, 10, 9), today))
                .isInstanceOf(UnderageOwner.class)
                .hasMessage("The owner must be an adult");
    }

    @Test
    void adultYearsIsEighteen() {
        assertThat(OwnerAge.ADULT_YEARS).isEqualTo(18);
    }
}
