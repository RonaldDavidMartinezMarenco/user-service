package com.foodcourt.user.domain.model;

import java.time.LocalDate;
import java.time.Period;

import com.foodcourt.user.domain.exception.UnderageOwner;

/**
 * An owner is an adult when the completed years are 18 or more.
 * The 18th birthday is the first accepted day. {@code birth_date} is nullable
 * in the table because only owners require it; this rule supplies that check.
 */
public final class OwnerAge {

    public static final int ADULT_YEARS = 18;

    private OwnerAge() {
    }

    public static void requireAdult(LocalDate birthDate, LocalDate today) {
        if (Period.between(birthDate, today).getYears() < ADULT_YEARS) {
            throw new UnderageOwner();
        }
    }
}
