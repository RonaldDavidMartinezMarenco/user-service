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

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.foodcourt.user.application.handler.CreateEmployeeHandler;
import com.foodcourt.user.domain.exception.AccessDenied;
import com.foodcourt.user.domain.exception.EmailAlreadyRegistered;
import com.foodcourt.user.domain.exception.EmployeeRoleRequired;
import com.foodcourt.user.domain.exception.IdentityDocumentAlreadyRegistered;
import com.foodcourt.user.domain.exception.RestaurantDirectoryUnavailable;
import com.foodcourt.user.domain.exception.RestaurantNotFound;
import com.foodcourt.user.domain.model.NewEmployee;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.infrastructure.config.JacksonConfig;
import com.foodcourt.user.infrastructure.exception.ApiExceptionHandler;
import com.foodcourt.user.infrastructure.security.SecurityConfig;
import com.foodcourt.user.infrastructure.security.SecurityProblemHandlers;

@WebMvcTest(EmployeeController.class)
@Import({ApiExceptionHandler.class, SecurityConfig.class, SecurityProblemHandlers.class, JacksonConfig.class})
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateEmployeeHandler handler;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void setUp() {
        reset(handler);
    }

    @Test
    void createReturnsTheEmployeeWithoutThePassword() throws Exception {
        when(handler.create(any(NewEmployee.class))).thenReturn(storedEmployee());

        postEmployee(validBody())
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/employees/30"))
                .andExpect(jsonPath("$.id").value(30))
                .andExpect(jsonPath("$.firstName").value("Luis"))
                .andExpect(jsonPath("$.lastName").value("Pérez"))
                .andExpect(jsonPath("$.identityDocument").value("1234567890"))
                .andExpect(jsonPath("$.phone").value("+573005698325"))
                .andExpect(jsonPath("$.email").value("luis@example.com"))
                .andExpect(jsonPath("$.role").value("EMPLOYEE"))
                .andExpect(jsonPath("$.restaurantId").value(8))
                .andExpect(jsonPath("$.birthDate").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void createPassesTheRawValuesToTheHandler() throws Exception {
        when(handler.create(any(NewEmployee.class))).thenReturn(storedEmployee());
        ArgumentCaptor<NewEmployee> sent = ArgumentCaptor.forClass(NewEmployee.class);

        postEmployee(validBody().replace("luis@example.com", "Luis@Example.com")).andExpect(status().isCreated());

        verify(handler).create(sent.capture());
        assertThat(sent.getValue().email()).isEqualTo("Luis@Example.com");
        assertThat(sent.getValue().password()).isEqualTo("secret12");
        assertThat(sent.getValue().roleId()).isEqualTo((short) 3);
        assertThat(sent.getValue().restaurantId()).isEqualTo(8L);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("rejectedBodies")
    void createRejectsAnInvalidBody(String caseName, String body) throws Exception {
        postEmployee(body).andExpect(status().isBadRequest());
        verify(handler, never()).create(any());
    }

    @Test
    void createRejectsARoleOtherThanEmployee() throws Exception {
        when(handler.create(any(NewEmployee.class))).thenThrow(new EmployeeRoleRequired());

        postEmployee(validBody().replace("\"roleId\": 3", "\"roleId\": 2"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Employee role required"))
                .andExpect(jsonPath("$.detail").value("The role must be EMPLOYEE"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void createRejectsARestaurantOfAnotherOwner() throws Exception {
        when(handler.create(any(NewEmployee.class))).thenThrow(new AccessDenied());

        postEmployee(validBody())
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("You do not have permission for this action"))
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void createRejectsAMissingRestaurant() throws Exception {
        when(handler.create(any(NewEmployee.class))).thenThrow(new RestaurantNotFound());

        postEmployee(validBody())
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Restaurant not found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void createRejectsADuplicateEmail() throws Exception {
        when(handler.create(any(NewEmployee.class))).thenThrow(new EmailAlreadyRegistered());

        postEmployee(validBody())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Email already registered"));
    }

    @Test
    void createRejectsADuplicateIdentityDocument() throws Exception {
        when(handler.create(any(NewEmployee.class))).thenThrow(new IdentityDocumentAlreadyRegistered());

        postEmployee(validBody())
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Identity document already registered"));
    }

    @Test
    void createReportsAnUnavailablePlazaService() throws Exception {
        when(handler.create(any(NewEmployee.class))).thenThrow(new RestaurantDirectoryUnavailable());

        postEmployee(validBody())
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Plaza service unavailable"))
                .andExpect(jsonPath("$.detail").value("The plaza service is unavailable"))
                .andExpect(jsonPath("$.status").value(503));
    }

    @Test
    void createRejectsAMissingToken() throws Exception {
        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.detail").value("Authentication is required"));
        verify(handler, never()).create(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ROLE_ADMINISTRATOR", "ROLE_EMPLOYEE", "ROLE_CUSTOMER"})
    void createRejectsACallerThatIsNotAnOwner(String authority) throws Exception {
        mockMvc.perform(post("/employees")
                        .with(jwt().authorities(new SimpleGrantedAuthority(authority)))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.detail").value("You do not have permission for this action"));
        verify(handler, never()).create(any());
    }

    private static Stream<Arguments> rejectedBodies() {
        return Stream.of(
                Arguments.of("invalid email", validBody().replace("luis@example.com", "not-an-email")),
                Arguments.of("blank email", validBody().replace("luis@example.com", "")),
                Arguments.of("phone longer than 13", validBody().replace("+573005698325", "+5730056983259")),
                Arguments.of("phone with letters", validBody().replace("+573005698325", "57abc")),
                Arguments.of("document with letters", validBody().replace("1234567890", "12A45")),
                Arguments.of("blank document", validBody().replace("1234567890", "")),
                Arguments.of("blank first name", validBody().replace("\"Luis\"", "\"\"")),
                Arguments.of("first name without a letter", validBody().replace("\"Luis\"", "\"123\"")),
                Arguments.of("last name with a sign", validBody().replace("Pérez", "Pérez!")),
                Arguments.of("first name longer than 80", validBody().replace("\"Luis\"", "\"" + "A".repeat(81) + "\"")),
                Arguments.of("password shorter than 8", validBody().replace("secret12", "1234567")),
                Arguments.of("password longer than 72", validBody().replace("secret12", "a".repeat(73))),
                Arguments.of("missing role", validBody().replace("\"roleId\": 3,", "")),
                Arguments.of("missing restaurant", validBody().replace(",\n  \"restaurantId\": 8", "")),
                Arguments.of("restaurant id zero", validBody().replace("\"restaurantId\": 8", "\"restaurantId\": 0")),
                Arguments.of("negative restaurant id", validBody().replace("\"restaurantId\": 8", "\"restaurantId\": -1")),
                Arguments.of("role that is not a number", validBody().replace("\"roleId\": 3", "\"roleId\": \"EMPLOYEE\"")),
                Arguments.of("role id as text", validBody().replace("\"roleId\": 3", "\"roleId\": \"3\"")),
                Arguments.of("role id as a decimal", validBody().replace("\"roleId\": 3", "\"roleId\": 3.0")));
    }

    private ResultActions postEmployee(String body) throws Exception {
        return mockMvc.perform(post("/employees")
                .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_OWNER")))
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private static String validBody() {
        return """
                {
                  "firstName": "Luis",
                  "lastName": "Pérez",
                  "identityDocument": "1234567890",
                  "phone": "+573005698325",
                  "email": "luis@example.com",
                  "password": "secret12",
                  "roleId": 3,
                  "restaurantId": 8
                }
                """;
    }

    private static User storedEmployee() {
        return new User(
                30L,
                "Luis",
                "Pérez",
                "1234567890",
                "+573005698325",
                null,
                "luis@example.com",
                "hashed-secret12",
                Role.EMPLOYEE,
                8L);
    }
}
