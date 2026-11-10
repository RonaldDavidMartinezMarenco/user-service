package com.foodcourt.user.application.handler;

import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.api.FindCustomerPhone;
import com.foodcourt.user.domain.model.CustomerPhone;
import com.foodcourt.user.domain.spi.CurrentCaller;

/** Entry the controller calls. This type reads the caller and forwards the id. */
@Service
public final class FindCustomerPhoneHandler {

    private final FindCustomerPhone findCustomerPhone;
    private final CurrentCaller currentCaller;

    public FindCustomerPhoneHandler(FindCustomerPhone findCustomerPhone, CurrentCaller currentCaller) {
        this.findCustomerPhone = findCustomerPhone;
        this.currentCaller = currentCaller;
    }

    public CustomerPhone find(long id) {
        return findCustomerPhone.find(currentCaller.require(), id);
    }
}
