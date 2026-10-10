package com.foodcourt.user.infrastructure.input.rest;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foodcourt.user.application.handler.CreateOwnerHandler;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.infrastructure.input.rest.dto.CreateOwnerRequest;
import com.foodcourt.user.infrastructure.input.rest.dto.OwnerResponse;
import com.foodcourt.user.infrastructure.input.rest.mapper.OwnerRestMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Creates an owner account. The class is not final: method security needs a CGLIB subclass.
 */
@RestController
@Tag(name = "Owners")
@SecurityRequirement(name = "bearerAuth")
class OwnerController {

    private final CreateOwnerHandler handler;
    private final OwnerRestMapper mapper = new OwnerRestMapper();

    OwnerController(CreateOwnerHandler handler) {
        this.handler = handler;
    }

    @PostMapping("/owners")
    @Operation(summary = "Create an owner account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Owner created"),
            @ApiResponse(responseCode = "400", description = "Invalid body or the owner is under 18"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid access token"),
            @ApiResponse(responseCode = "403", description = "The caller is not an administrator"),
            @ApiResponse(responseCode = "409", description = "Email or identity document already registered")
    })
    @PreAuthorize("hasRole('ADMINISTRATOR')")
    public ResponseEntity<OwnerResponse> create(@Valid @RequestBody CreateOwnerRequest request) {
        User owner = handler.create(mapper.toNewOwner(request));
        URI location = URI.create("/owners/" + owner.id());
        return ResponseEntity.created(location).body(mapper.toResponse(owner));
    }
}
