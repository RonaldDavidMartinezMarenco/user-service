package com.foodcourt.user.infrastructure.input.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.foodcourt.user.application.handler.LoginHandler;
import com.foodcourt.user.infrastructure.input.rest.dto.LoginRequest;
import com.foodcourt.user.infrastructure.input.rest.dto.LoginResponse;
import com.foodcourt.user.infrastructure.input.rest.mapper.LoginRestMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/** Exchanges email and password for an access token. This route stays public. */
@RestController
@Tag(name = "Login")
final class LoginController {

    private final LoginHandler handler;
    private final LoginRestMapper mapper = new LoginRestMapper();

    LoginController(LoginHandler handler) {
        this.handler = handler;
    }

    @PostMapping("/login")
    @Operation(summary = "Log in with email and password")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Access token issued"),
            @ApiResponse(responseCode = "400", description = "Invalid email or password length"),
            @ApiResponse(responseCode = "401", description = "Unknown email or wrong password")
    })
    ResponseEntity<LoginResponse> login(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true, content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = LoginRequest.class),
                    examples = @ExampleObject(name = "Carlos", value = """
                            {
                              "email": "carlos.ramirez@example.com",
                              "password": "Password123"
                            }
                            """)))
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(mapper.toResponse(handler.login(mapper.toCredentials(request))));
    }
}
