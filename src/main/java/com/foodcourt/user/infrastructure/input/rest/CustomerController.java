package com.foodcourt.user.infrastructure.input.rest;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foodcourt.user.application.handler.CreateCustomerHandler;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.infrastructure.input.rest.dto.CreateCustomerRequest;
import com.foodcourt.user.infrastructure.input.rest.dto.CustomerResponse;
import com.foodcourt.user.infrastructure.input.rest.mapper.CustomerRestMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Registers a customer account. The call is public: the customer has no access token yet.
 * {@code Location} points at {@code GET /users/{id}}, the account resource that already exists.
 */
@RestController
@Tag(name = "Customers")
final class CustomerController {

    private final CreateCustomerHandler handler;
    private final CustomerRestMapper mapper = new CustomerRestMapper();

    CustomerController(CreateCustomerHandler handler) {
        this.handler = handler;
    }

    @PostMapping("/customers")
    @Operation(summary = "Register a customer account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Customer registered"),
            @ApiResponse(responseCode = "400", description = "Invalid body, person name, password, or a role other than CUSTOMER"),
            @ApiResponse(responseCode = "409", description = "Email or identity document already registered"),
            @ApiResponse(responseCode = "415", description = "The body is not JSON")
    })
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request) {
        User customer = handler.create(mapper.toNewCustomer(request));
        URI location = URI.create("/users/" + customer.id());
        return ResponseEntity.created(location).body(mapper.toResponse(customer));
    }
}
