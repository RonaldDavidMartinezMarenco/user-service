package com.foodcourt.user.infrastructure.input.rest;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foodcourt.user.application.handler.CreateEmployeeHandler;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.infrastructure.input.rest.dto.CreateEmployeeRequest;
import com.foodcourt.user.infrastructure.input.rest.dto.EmployeeResponse;
import com.foodcourt.user.infrastructure.input.rest.mapper.EmployeeRestMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/**
 * Creates an employee account for a restaurant of the calling owner.
 * The class is not final: method security needs a CGLIB subclass.
 */
@RestController
@Tag(name = "Employees")
@SecurityRequirement(name = "bearerAuth")
class EmployeeController {

    private final CreateEmployeeHandler handler;
    private final EmployeeRestMapper mapper = new EmployeeRestMapper();

    EmployeeController(CreateEmployeeHandler handler) {
        this.handler = handler;
    }

    @PostMapping("/employees")
    @Operation(summary = "Create an employee account")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Employee created"),
            @ApiResponse(responseCode = "400", description = "Invalid body, person name, or a role other than EMPLOYEE"),
            @ApiResponse(responseCode = "401", description = "Missing or invalid access token"),
            @ApiResponse(responseCode = "403", description = "The caller is not the owner of this restaurant"),
            @ApiResponse(responseCode = "404", description = "No restaurant exists for this id"),
            @ApiResponse(responseCode = "409", description = "Email or identity document already registered"),
            @ApiResponse(responseCode = "503", description = "The plaza service is unavailable")
    })
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<EmployeeResponse> create(@Valid @RequestBody CreateEmployeeRequest request) {
        User employee = handler.create(mapper.toNewEmployee(request));
        URI location = URI.create("/employees/" + employee.id());
        return ResponseEntity.created(location).body(mapper.toResponse(employee));
    }
}
