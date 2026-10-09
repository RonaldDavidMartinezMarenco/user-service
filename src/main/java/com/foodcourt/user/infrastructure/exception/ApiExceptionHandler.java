package com.foodcourt.user.infrastructure.exception;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.foodcourt.user.domain.exception.EmailAlreadyRegistered;
import com.foodcourt.user.domain.exception.IdentityDocumentAlreadyRegistered;
import com.foodcourt.user.domain.exception.UnderageOwner;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Maps domain failures to {@code application/problem+json}.
 * Bean Validation is left to Spring, which already returns HTTP 400.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(UnderageOwner.class)
    ProblemDetail underage(UnderageOwner exception, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "Underage owner", exception, request);
    }

    @ExceptionHandler(EmailAlreadyRegistered.class)
    ProblemDetail emailTaken(EmailAlreadyRegistered exception, HttpServletRequest request) {
        return problem(HttpStatus.CONFLICT, "Email already registered", exception, request);
    }

    @ExceptionHandler(IdentityDocumentAlreadyRegistered.class)
    ProblemDetail documentTaken(IdentityDocumentAlreadyRegistered exception, HttpServletRequest request) {
        return problem(HttpStatus.CONFLICT, "Identity document already registered", exception, request);
    }

    private static ProblemDetail problem(
            HttpStatus status,
            String title,
            RuntimeException exception,
            HttpServletRequest request) {
        ProblemDetail body = ProblemDetail.forStatusAndDetail(status, exception.getMessage());
        body.setTitle(title);
        body.setInstance(URI.create(request.getRequestURI()));
        return body;
    }
}
