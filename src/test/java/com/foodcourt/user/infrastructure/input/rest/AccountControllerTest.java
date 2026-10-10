package com.foodcourt.user.infrastructure.input.rest;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.foodcourt.user.application.handler.FindAccountHandler;
import com.foodcourt.user.domain.exception.UserNotFound;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.infrastructure.exception.ApiExceptionHandler;
import com.foodcourt.user.infrastructure.security.SecurityConfig;
import com.foodcourt.user.infrastructure.security.SecurityProblemHandlers;

@WebMvcTest(AccountController.class)
@Import({ApiExceptionHandler.class, SecurityConfig.class, SecurityProblemHandlers.class})
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FindAccountHandler handler;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void findReturnsTheIdAndRoleWithoutThePassword() throws Exception {
        when(handler.find(15L)).thenReturn(storedOwner());

        mockMvc.perform(get("/users/15").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(15))
                .andExpect(jsonPath("$.role").value("OWNER"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist())
                .andExpect(jsonPath("$.email").doesNotExist());
    }

    @Test
    void findRejectsAMissingAccount() throws Exception {
        when(handler.find(15L)).thenThrow(new UserNotFound());

        mockMvc.perform(get("/users/15").with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMINISTRATOR"))))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("User not found"))
                .andExpect(jsonPath("$.detail").value("No account exists for this id"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void findRejectsAMissingToken() throws Exception {
        mockMvc.perform(get("/users/15"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.detail").value("Authentication is required"));
        verify(handler, never()).find(15L);
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
