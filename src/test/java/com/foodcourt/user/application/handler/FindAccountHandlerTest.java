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
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class FindAccountHandlerTest {

    @Mock
    private FindAccount findAccount;

    @Test
    void findForwardsTheIdAndReturnsTheAccount() {
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
        when(findAccount.find(15L)).thenReturn(stored);
        FindAccountHandler handler = new FindAccountHandler(findAccount);

        User result = handler.find(15L);

        assertThat(result).isEqualTo(stored);
        verify(findAccount).find(15L);
    }
}
