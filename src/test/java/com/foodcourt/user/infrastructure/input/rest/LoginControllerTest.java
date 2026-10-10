package com.foodcourt.user.infrastructure.input.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.foodcourt.user.application.handler.LoginHandler;
import com.foodcourt.user.domain.exception.InvalidCredentials;
import com.foodcourt.user.domain.model.AccessToken;
import com.foodcourt.user.domain.model.Credentials;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.infrastructure.exception.ApiExceptionHandler;
import com.foodcourt.user.infrastructure.security.SecurityConfig;
import com.foodcourt.user.infrastructure.security.SecurityProblemHandlers;

@WebMvcTest(LoginController.class)
@Import({ApiExceptionHandler.class, SecurityConfig.class, SecurityProblemHandlers.class})
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginHandler handler;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void loginReturnsTheTokenWithoutThePassword() throws Exception {
        when(handler.login(any(Credentials.class)))
                .thenReturn(new AccessToken("signed-token", 28800L, 15L, Role.OWNER));

        postLogin(body("ana@example.com", "secret12"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.accessToken").value("signed-token"))
                .andExpect(jsonPath("$.expiresIn").value(28800))
                .andExpect(jsonPath("$.userId").value(15))
                .andExpect(jsonPath("$.role").value("OWNER"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void loginRejectsAShortPasswordBeforeTheHandler() throws Exception {
        postLogin(body("ana@example.com", "short"))
                .andExpect(status().isBadRequest());
        verify(handler, never()).login(any());
    }

    @Test
    void loginRejectsAPasswordLongerThan72BeforeTheHandler() throws Exception {
        postLogin(body("ana@example.com", "a".repeat(73)))
                .andExpect(status().isBadRequest());
        verify(handler, never()).login(any());
    }

    @Test
    void loginRejectsAnInvalidEmailBeforeTheHandler() throws Exception {
        postLogin(body("not-an-email", "secret12"))
                .andExpect(status().isBadRequest());
        verify(handler, never()).login(any());
    }

    @Test
    void loginRejectsUnknownCredentials() throws Exception {
        when(handler.login(any(Credentials.class))).thenThrow(new InvalidCredentials());

        postLogin(body("ana@example.com", "secret12"))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("$.title").value("Invalid credentials"))
                .andExpect(jsonPath("$.detail").value("Invalid email or password"))
                .andExpect(jsonPath("$.status").value(401));
    }

    private ResultActions postLogin(String body) throws Exception {
        return mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }

    private static String body(String email, String password) {
        return """
                {
                  "email": "%s",
                  "password": "%s"
                }
                """.formatted(email, password);
    }
}
