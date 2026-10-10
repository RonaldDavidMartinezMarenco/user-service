package com.foodcourt.user.infrastructure.input.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.foodcourt.user.application.handler.FindAccountHandler;
import com.foodcourt.user.infrastructure.input.rest.dto.AccountResponse;
import com.foodcourt.user.infrastructure.input.rest.mapper.AccountRestMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * Reads an account by id. The access token is required.
 * An administrator may read any id. Any other caller may read only their own.
 */
@RestController
@Tag(name = "Accounts")
@SecurityRequirement(name = "bearerAuth")
final class AccountController {

    private final FindAccountHandler handler;
    private final AccountRestMapper mapper = new AccountRestMapper();

    AccountController(FindAccountHandler handler) {
        this.handler = handler;
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Find an account by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Account found"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid access token"),
            @ApiResponse(responseCode = "403", description = "The caller may not read this account"),
            @ApiResponse(responseCode = "404", description = "No account exists for this id")
    })
    ResponseEntity<AccountResponse> find(@PathVariable long id) {
        return ResponseEntity.ok(mapper.toResponse(handler.find(id)));
    }
}
