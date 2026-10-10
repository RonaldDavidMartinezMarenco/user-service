package com.foodcourt.user.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodcourt.user.domain.api.Login;
import com.foodcourt.user.domain.model.AccessToken;
import com.foodcourt.user.domain.model.Credentials;
import com.foodcourt.user.domain.model.Role;

@ExtendWith(MockitoExtension.class)
class LoginHandlerTest {

    @Mock
    private Login login;

    @Test
    void loginForwardsTheCredentialsAndReturnsTheToken() {
        Credentials credentials = new Credentials("ana@example.com", "secret12");
        AccessToken issued = new AccessToken("signed", 28800L, 15L, Role.OWNER);
        when(login.login(credentials)).thenReturn(issued);
        LoginHandler handler = new LoginHandler(login);

        AccessToken result = handler.login(credentials);

        assertThat(result).isEqualTo(issued);
        verify(login).login(credentials);
    }
}
