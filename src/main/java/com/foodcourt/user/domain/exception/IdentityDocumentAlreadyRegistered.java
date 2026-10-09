package com.foodcourt.user.domain.exception;

/** {@code app_user.identity_document} is already taken. Leading zeros are significant. */
public final class IdentityDocumentAlreadyRegistered extends RuntimeException {

    public IdentityDocumentAlreadyRegistered() {
        super("Identity document is already registered");
    }
}
