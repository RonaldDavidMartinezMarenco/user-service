package com.foodcourt.user.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodcourt.user.domain.api.CreateEmployee;
import com.foodcourt.user.domain.model.Caller;
import com.foodcourt.user.domain.model.NewEmployee;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.CurrentCaller;

@ExtendWith(MockitoExtension.class)
class CreateEmployeeHandlerTest {

    private static final Caller OWNER = new Caller(15L, Role.OWNER);

    @Mock
    private CreateEmployee createEmployee;

    @Mock
    private CurrentCaller currentCaller;

    @Test
    void createForwardsTheCallerAndTheEmployee() {
        NewEmployee incoming = new NewEmployee(
                "Luis",
                "Pérez",
                "1234567890",
                "+573005698325",
                "luis@example.com",
                "secret12",
                Role.EMPLOYEE.id(),
                8L);
        User stored = new User(
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
        when(currentCaller.require()).thenReturn(OWNER);
        when(createEmployee.create(OWNER, incoming)).thenReturn(stored);
        CreateEmployeeHandler handler = new CreateEmployeeHandler(createEmployee, currentCaller);

        User result = handler.create(incoming);

        assertThat(result).isEqualTo(stored);
        verify(createEmployee).create(OWNER, incoming);
    }
}
