package com.foodcourt.user.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodcourt.user.domain.api.FindAccount;
import com.foodcourt.user.domain.model.Caller;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.CurrentCaller;

@ExtendWith(MockitoExtension.class)
class FindAccountHandlerTest {

    private static final Caller ADMINISTRATOR = new Caller(1L, Role.ADMINISTRATOR);

    @Mock
    private FindAccount findAccount;

    @Mock
    private CurrentCaller currentCaller;

    @Test
    void findForwardsTheCallerAndTheId() {
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
        when(currentCaller.require()).thenReturn(ADMINISTRATOR);
        when(findAccount.find(ADMINISTRATOR, 15L)).thenReturn(stored);
        FindAccountHandler handler = new FindAccountHandler(findAccount, currentCaller);

        User result = handler.find(15L);

        assertThat(result).isEqualTo(stored);
        verify(findAccount).find(ADMINISTRATOR, 15L);
    }
}
