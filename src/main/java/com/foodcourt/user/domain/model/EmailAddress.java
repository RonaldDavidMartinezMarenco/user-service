package com.foodcourt.user.domain.model;

import java.util.Locale;

/**
 * Mailbox compared and stored in lower case.
 * The unique index on {@code app_user.email} is case-sensitive, so
 * {@code Ana@Example.com} and {@code ana@example.com} would otherwise both fit.
 */
public record EmailAddress(String value) {

    public EmailAddress {
        value = value.toLowerCase(Locale.ROOT);
    }
}
