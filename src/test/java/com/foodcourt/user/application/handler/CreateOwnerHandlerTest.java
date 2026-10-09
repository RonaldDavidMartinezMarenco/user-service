package com.foodcourt.user.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodcourt.user.domain.api.CreateOwner;
import com.foodcourt.user.domain.model.NewOwner;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class CreateOwnerHandlerTest {

    @Mock
    private CreateOwner createOwner;

    @Test
    void createForwardsTheOwnerAndReturnsTheStoredAccount() {
        NewOwner incoming = new NewOwner(
                "María",
                "Ana2",
                "1234567890",
                "+573005698325",
                LocalDate.of(1990, 5, 1),
                "ana@example.com",
                "secret12");
        User stored = new User(
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
        when(createOwner.create(incoming)).thenReturn(stored);
        CreateOwnerHandler handler = new CreateOwnerHandler(createOwner);

        User result = handler.create(incoming);

        assertThat(result).isEqualTo(stored);
        verify(createOwner).create(incoming);
    }
}
