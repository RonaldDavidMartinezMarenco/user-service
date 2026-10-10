package com.foodcourt.user.infrastructure.input.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.foodcourt.user.application.handler.CreateOwnerHandler;
import com.foodcourt.user.domain.exception.EmailAlreadyRegistered;
import com.foodcourt.user.domain.exception.IdentityDocumentAlreadyRegistered;
import com.foodcourt.user.domain.exception.InvalidPersonName;
import com.foodcourt.user.domain.exception.UnderageOwner;
import com.foodcourt.user.domain.model.NewOwner;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.infrastructure.exception.ApiExceptionHandler;
import com.foodcourt.user.infrastructure.security.SecurityConfig;
import com.foodcourt.user.infrastructure.security.SecurityProblemHandlers;

@WebMvcTest(OwnerController.class)
@Import({ApiExceptionHandler.class, SecurityConfig.class, SecurityProblemHandlers.class})
class OwnerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateOwnerHandler handler;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        reset(handler);
    }

    @Test
    void createReturnsTheOwnerWithoutThePassword() throws Exception {
        when(handler.create(any(NewOwner.class))).thenReturn(storedOwner());

        postOwner(validBody("Ana@Example.com", "secret12", "María", "Ana2"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/owners/15"))
                .andExpect(jsonPath("$.id").value(15))
                .andExpect(jsonPath("$.firstName").value("María"))
                .andExpect(jsonPath("$.lastName").value("Ana2"))
                .andExpect(jsonPath("$.identityDocument").value("1234567890"))
                .andExpect(jsonPath("$.phone").value("+573005698325"))
                .andExpect(jsonPath("$.birthDate").value("1990-05-01"))
                .andExpect(jsonPath("$.email").value("ana@example.com"))
                .andExpect(jsonPath("$.role").value("OWNER"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void createPassesTheRawPasswordAndEmailToTheHandler() throws Exception {
        when(handler.create(any(NewOwner.class))).thenReturn(storedOwner());
        org.mockito.ArgumentCaptor<NewOwner> sent = org.mockito.ArgumentCaptor.forClass(NewOwner.class);

        postOwner(validBody("Ana@Example.com", "secret12", "María", "Ana2"))
                .andExpect(status().isCreated());

        verify(handler).create(sent.capture());
        assertThat(sent.getValue().email()).isEqualTo("Ana@Example.com");
        assertThat(sent.getValue().password()).isEqualTo("secret12");
        assertThat(sent.getValue().firstName()).isEqualTo("María");
        assertThat(sent.getValue().lastName()).isEqualTo("Ana2");
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rejectedBodies")
    void createRejectsAnInvalidBody(String caseName, String body) throws Exception {
        postOwner(body).andExpect(status().isBadRequest());
        verify(handler, never()).create(any());
    }

    @Test
    void createRejectsAnInvalidPersonName() throws Exception {
        when(handler.create(any(NewOwner.class))).thenThrow(new InvalidPersonName());

        postOwner(validBody("ana@example.com", "secret12", "María", "Ana2"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Invalid person name"))
                .andExpect(jsonPath("$.detail").value("The name must contain a letter and only the allowed characters"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createRejectsAnUnderageOwner() throws Exception {
        when(handler.create(any(NewOwner.class))).thenThrow(new UnderageOwner());

        postOwner(validBody("ana@example.com", "secret12", "María", "Ana2"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Underage owner"))
                .andExpect(jsonPath("$.detail").value("The owner must be an adult"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createRejectsADuplicateEmail() throws Exception {
        when(handler.create(any(NewOwner.class))).thenThrow(new EmailAlreadyRegistered());

        postOwner(validBody("ana@example.com", "secret12", "María", "Ana2"))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Email already registered"))
                .andExpect(jsonPath("$.detail").value("Email is already registered"))
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void createRejectsADuplicateIdentityDocument() throws Exception {
        when(handler.create(any(NewOwner.class))).thenThrow(new IdentityDocumentAlreadyRegistered());

        postOwner(validBody("ana@example.com", "secret12", "María", "Ana2"))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Identity document already registered"))
                .andExpect(jsonPath("$.detail").value("Identity document is already registered"))
                .andExpect(jsonPath("$.status").value(409));
    }

    private static Stream<Arguments> rejectedBodies() {
        return Stream.of(
                Arguments.of("invalid email", validBody("not-an-email", "secret12", "María", "Ana2")),
                Arguments.of("phone longer than 13", validBody("ana@example.com", "secret12", "María", "Ana2")
                        .replace("+573005698325", "+5730056983259")),
                Arguments.of("phone with letters", validBody("ana@example.com", "secret12", "María", "Ana2")
                        .replace("+573005698325", "57abc")),
                Arguments.of("document with letters", validBody("ana@example.com", "secret12", "María", "Ana2")
                        .replace("1234567890", "12A45")),
                Arguments.of("blank first name", validBody("ana@example.com", "secret12", "", "Ana2")),
                Arguments.of("missing birth date", validBody("ana@example.com", "secret12", "María", "Ana2")
                        .replace("\"birthDate\": \"1990-05-01\",", "")),
                Arguments.of("password shorter than 8", validBody("ana@example.com", "1234567", "María", "Ana2")),
                Arguments.of("password longer than 72", validBody("ana@example.com", "a".repeat(73), "María", "Ana2")),
                Arguments.of("first name without a letter", validBody("ana@example.com", "secret12", "123", "Ana2")),
                Arguments.of("last name without a letter", validBody("ana@example.com", "secret12", "María", "123")),
                Arguments.of("first name with a sign", validBody("ana@example.com", "secret12", "Ana!", "Ana2")),
                Arguments.of("symbols only", validBody("ana@example.com", "secret12", "***", "Ana2")),
                Arguments.of("first name longer than 80", validBody("ana@example.com", "secret12", "A".repeat(81), "Ana2")),
                Arguments.of("malformed birth date", validBody("ana@example.com", "secret12", "María", "Ana2")
                        .replace("1990-05-01", "not-a-date")));
    }

    @Test
    void createRejectsAMissingToken() throws Exception {
        mockMvc.perform(post("/owners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody("ana@example.com", "secret12", "María", "Ana2")))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Unauthorized"))
                .andExpect(jsonPath("$.detail").value("Authentication is required"));
        verify(handler, never()).create(any());
    }

    @Test
    void createRejectsANonAdministrator() throws Exception {
        mockMvc.perform(post("/owners")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OWNER")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody("ana@example.com", "secret12", "María", "Ana2")))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("You do not have permission for this action"));
        verify(handler, never()).create(any());
    }

    private ResultActions postOwner(String body) throws Exception {
        return mockMvc.perform(post("/owners")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private static String validBody(String email, String password, String firstName, String lastName) {
        return """
                {
                  "firstName": "%s",
                  "lastName": "%s",
                  "identityDocument": "1234567890",
                  "phone": "+573005698325",
                  "birthDate": "1990-05-01",
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(firstName, lastName, email, password);
    }

    private static User storedOwner() {
        return new User(
                15L,
                "María",
                "Ana2",
                "1234567890",
                "+573005698325",
                LocalDate.of(1990, 5, 1),
                "ana@example.com",
                "hashed-secret12",
                Role.OWNER,
                null);
    }
}
