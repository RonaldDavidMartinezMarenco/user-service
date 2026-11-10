package com.foodcourt.user.application.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.foodcourt.user.domain.api.FindCustomerPhone;
import com.foodcourt.user.domain.model.Caller;
import com.foodcourt.user.domain.model.CustomerPhone;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.spi.CurrentCaller;

@ExtendWith(MockitoExtension.class)
class FindCustomerPhoneHandlerTest {

    private static final Caller EMPLOYEE = new Caller(5L, Role.EMPLOYEE);

    @Mock
    private FindCustomerPhone findCustomerPhone;

    @Mock
    private CurrentCaller currentCaller;

    @Test
    void findForwardsTheCallerAndTheId() {
        CustomerPhone phone = new CustomerPhone("+573011590640");
        when(currentCaller.require()).thenReturn(EMPLOYEE);
        when(findCustomerPhone.find(EMPLOYEE, 9L)).thenReturn(phone);
        FindCustomerPhoneHandler handler = new FindCustomerPhoneHandler(findCustomerPhone, currentCaller);

        CustomerPhone result = handler.find(9L);

        assertThat(result).isEqualTo(phone);
        verify(findCustomerPhone).find(EMPLOYEE, 9L);
    }
}
