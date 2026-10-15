package com.foodcourt.user.infrastructure.exception;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.foodcourt.user.domain.exception.AccessDenied;
import com.foodcourt.user.domain.exception.AuthenticationRequired;
import com.foodcourt.user.domain.exception.CustomerRoleRequired;
import com.foodcourt.user.domain.exception.EmailAlreadyRegistered;
import com.foodcourt.user.domain.exception.EmployeeRoleRequired;
import com.foodcourt.user.domain.exception.InvalidCredentials;
import com.foodcourt.user.domain.exception.InvalidPersonName;
import com.foodcourt.user.domain.exception.IdentityDocumentAlreadyRegistered;
import com.foodcourt.user.domain.exception.PasswordTooLong;
import com.foodcourt.user.domain.exception.RestaurantDirectoryUnavailable;
import com.foodcourt.user.domain.exception.RestaurantNotFound;
import com.foodcourt.user.domain.exception.UnderageOwner;
import com.foodcourt.user.domain.exception.UserNotFound;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

/**
 * Maps domain failures to {@code application/problem+json}.
 * Bean Validation is left to Spring, which already returns HTTP 400.
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(InvalidCredentials.class)
    ProblemDetail invalidCredentials(InvalidCredentials exception, HttpServletRequest request) {
        return problem(HttpStatus.UNAUTHORIZED, "Invalid credentials", exception, request);
    }

    @ExceptionHandler(AuthenticationRequired.class)
    ProblemDetail authenticationRequired(AuthenticationRequired exception, HttpServletRequest request) {
        return problem(HttpStatus.UNAUTHORIZED, "Unauthorized", exception, request);
    }

    @ExceptionHandler(AccessDenied.class)
    ProblemDetail accessDenied(AccessDenied exception, HttpServletRequest request) {
        return problem(HttpStatus.FORBIDDEN, "Forbidden", exception, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail invalidConstraint(ConstraintViolationException exception, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid request", exception, request);
    }

    @ExceptionHandler(UserNotFound.class)
    ProblemDetail missing(UserNotFound exception, HttpServletRequest request) {
        return problem(HttpStatus.NOT_FOUND, "User not found", exception, request);
    }

    @ExceptionHandler(InvalidPersonName.class)
    ProblemDetail invalidName(InvalidPersonName exception, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "Invalid person name", exception, request);
    }

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

    @ExceptionHandler(EmployeeRoleRequired.class)
    ProblemDetail wrongRole(EmployeeRoleRequired exception, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "Employee role required", exception, request);
    }

    @ExceptionHandler(CustomerRoleRequired.class)
    ProblemDetail wrongCustomerRole(CustomerRoleRequired exception, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "Customer role required", exception, request);
    }

    @ExceptionHandler(PasswordTooLong.class)
    ProblemDetail passwordTooLong(PasswordTooLong exception, HttpServletRequest request) {
        return problem(HttpStatus.BAD_REQUEST, "Password too long", exception, request);
    }

    @ExceptionHandler(RestaurantNotFound.class)
    ProblemDetail missingRestaurant(RestaurantNotFound exception, HttpServletRequest request) {
        return problem(HttpStatus.NOT_FOUND, "Restaurant not found", exception, request);
    }

    @ExceptionHandler(RestaurantDirectoryUnavailable.class)
    ProblemDetail plazaDown(RestaurantDirectoryUnavailable exception, HttpServletRequest request) {
        return problem(HttpStatus.SERVICE_UNAVAILABLE, "Plaza service unavailable", exception, request);
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
