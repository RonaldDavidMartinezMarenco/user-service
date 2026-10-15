package com.foodcourt.user.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodcourt.user.domain.api.CreateCustomer;
import com.foodcourt.user.domain.model.NewCustomer;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;

@ExtendWith(MockitoExtension.class)
class CreateCustomerHandlerTest {

    @Mock
    private CreateCustomer createCustomer;

    @Test
    void createForwardsTheCustomer() {
        NewCustomer incoming = new NewCustomer(
                "Ana",
                "Gómez",
                "1098765432",
                "+573001112233",
                "ana@example.com",
                "secret12",
                Role.CUSTOMER.id());
        User stored = new User(
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
        when(createCustomer.create(incoming)).thenReturn(stored);
        CreateCustomerHandler handler = new CreateCustomerHandler(createCustomer);

        User result = handler.create(incoming);

        assertThat(result).isEqualTo(stored);
        verify(createCustomer).create(incoming);
    }
}
