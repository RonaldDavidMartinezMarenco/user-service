package com.foodcourt.user.infrastructure.input.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.BadJwtException;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.foodcourt.user.application.handler.CreateCustomerHandler;
import com.foodcourt.user.domain.exception.CustomerRoleRequired;
import com.foodcourt.user.domain.exception.EmailAlreadyRegistered;
import com.foodcourt.user.domain.exception.IdentityDocumentAlreadyRegistered;
import com.foodcourt.user.domain.exception.InvalidPersonName;
import com.foodcourt.user.domain.exception.PasswordTooLong;
import com.foodcourt.user.domain.model.NewCustomer;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.infrastructure.config.JacksonConfig;
import com.foodcourt.user.infrastructure.exception.ApiExceptionHandler;
import com.foodcourt.user.infrastructure.security.SecurityConfig;
import com.foodcourt.user.infrastructure.security.SecurityProblemHandlers;

@WebMvcTest(CustomerController.class)
@Import({ApiExceptionHandler.class, SecurityConfig.class, SecurityProblemHandlers.class, JacksonConfig.class})
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateCustomerHandler handler;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        reset(handler);
    }

    @Test
    void createReturnsTheCustomerWithoutAToken() throws Exception {
        when(handler.create(any(NewCustomer.class))).thenReturn(storedCustomer());

        postCustomer(validBody())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/users/31"))
                .andExpect(jsonPath("$.id").value(31))
                .andExpect(jsonPath("$.firstName").value("Ana"))
                .andExpect(jsonPath("$.lastName").value("Gómez"))
                .andExpect(jsonPath("$.identityDocument").value("1098765432"))
                .andExpect(jsonPath("$.phone").value("+573001112233"))
                .andExpect(jsonPath("$.email").value("ana@example.com"))
                .andExpect(jsonPath("$.role").value("CUSTOMER"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.restaurantId").doesNotExist())
                .andExpect(jsonPath("$.birthDate").doesNotExist());
    }

    @Test
    void createPassesTheRawValuesToTheHandler() throws Exception {
        when(handler.create(any(NewCustomer.class))).thenReturn(storedCustomer());
        ArgumentCaptor<NewCustomer> sent = ArgumentCaptor.forClass(NewCustomer.class);

        postCustomer(validBody().replace("ana@example.com", "Ana@Example.com")).andExpect(status().isCreated());

        verify(handler).create(sent.capture());
        assertThat(sent.getValue().email()).isEqualTo("Ana@Example.com");
        assertThat(sent.getValue().password()).isEqualTo("secret12");
        assertThat(sent.getValue().roleId()).isEqualTo((short) 4);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rejectedBodies")
    void createRejectsAnInvalidBody(String caseName, String body) throws Exception {
        postCustomer(body).andExpect(status().isBadRequest());
        verify(handler, never()).create(any());
    }

    @Test
    void createRejectsARoleOtherThanCustomer() throws Exception {
        when(handler.create(any(NewCustomer.class))).thenThrow(new CustomerRoleRequired());

        postCustomer(validBody().replace("\"roleId\": 4", "\"roleId\": 1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Customer role required"))
                .andExpect(jsonPath("$.detail").value("The role must be CUSTOMER"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createRejectsAPasswordBcryptCannotStore() throws Exception {
        when(handler.create(any(NewCustomer.class))).thenThrow(new PasswordTooLong());

        postCustomer(validBody())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Password too long"))
                .andExpect(jsonPath("$.detail").value("The password must not exceed 72 bytes"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createRejectsAnInvalidPersonName() throws Exception {
        when(handler.create(any(NewCustomer.class))).thenThrow(new InvalidPersonName());

        postCustomer(validBody())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Invalid person name"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createRejectsADuplicateEmail() throws Exception {
        when(handler.create(any(NewCustomer.class))).thenThrow(new EmailAlreadyRegistered());

        postCustomer(validBody())
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Email already registered"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void createRejectsADuplicateIdentityDocument() throws Exception {
        when(handler.create(any(NewCustomer.class))).thenThrow(new IdentityDocumentAlreadyRegistered());

        postCustomer(validBody())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Identity document already registered"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void createRejectsAnInvalidToken() throws Exception {
        when(jwtDecoder.decode(anyString())).thenThrow(new BadJwtException("invalid"));

        mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer not-a-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("The access token is invalid"));
        verify(handler, never()).create(any());
    }

    @Test
    void createRejectsABodyThatIsNotJson() throws Exception {
        mockMvc.perform(post("/customers")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content(validBody()))
                .andExpect(status().isUnsupportedMediaType());
        verify(handler, never()).create(any());
    }

    private static Stream<Arguments> rejectedBodies() {
        return Stream.of(
                Arguments.of("invalid email", validBody().replace("ana@example.com", "not-an-email")),
                Arguments.of("blank email", validBody().replace("ana@example.com", "")),
                Arguments.of("email longer than 254", validBody().replace("ana@example.com", "a".repeat(243) + "@example.com")),
                Arguments.of("phone longer than 13", validBody().replace("+573001112233", "+5730011122339")),
                Arguments.of("phone with letters", validBody().replace("+573001112233", "57abc")),
                Arguments.of("phone shorter than 7", validBody().replace("+573001112233", "+12345")),
                Arguments.of("document with letters", validBody().replace("1098765432", "12A45")),
                Arguments.of("blank document", validBody().replace("1098765432", "")),
                Arguments.of("document longer than 20", validBody().replace("1098765432", "1".repeat(21))),
                Arguments.of("blank first name", validBody().replace("\"Ana\"", "\"\"")),
                Arguments.of("first name without a letter", validBody().replace("\"Ana\"", "\"123\"")),
                Arguments.of("last name with a sign", validBody().replace("Gómez", "Gómez!")),
                Arguments.of("first name longer than 80", validBody().replace("\"Ana\"", "\"" + "A".repeat(81) + "\"")),
                Arguments.of("password shorter than 8", validBody().replace("secret12", "1234567")),
                Arguments.of("password longer than 72 characters", validBody().replace("secret12", "a".repeat(73))),
                Arguments.of("missing role", validBody().replace(",\n  \"roleId\": 4", "")),
                Arguments.of("role id as text", validBody().replace("\"roleId\": 4", "\"roleId\": \"4\"")),
                Arguments.of("role id as a decimal", validBody().replace("\"roleId\": 4", "\"roleId\": 4.5")),
                Arguments.of("role that is not a number", validBody().replace("\"roleId\": 4", "\"roleId\": \"CUSTOMER\"")),
                Arguments.of("unknown restaurant", validBody().replace("\n}", ",\n  \"restaurantId\": 5\n}")),
                Arguments.of("unknown role name", validBody().replace("\n}", ",\n  \"role\": \"ADMINISTRATOR\"\n}")),
                Arguments.of("unknown birth date", validBody().replace("\n}", ",\n  \"birthDate\": \"2000-01-01\"\n}")),
                Arguments.of("malformed json", "{"),
                Arguments.of("empty body", ""));
    }

    private ResultActions postCustomer(String body) throws Exception {
        return mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private static String validBody() {
        return """
                {
                  "firstName": "Ana",
                  "lastName": "Gómez",
                  "identityDocument": "1098765432",
                  "phone": "+573001112233",
                  "email": "ana@example.com",
                  "password": "secret12",
                  "roleId": 4
                }
                """;
    }

    private static User storedCustomer() {
        return new User(
                31L,
                "Ana",
                "Gómez",
                "1098765432",
                "+573001112233",
                null,
                "ana@example.com",
                "hashed-secret12",
                Role.CUSTOMER,
                null);
    }
}
