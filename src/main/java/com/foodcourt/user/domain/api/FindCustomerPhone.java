package com.foodcourt.user.domain.api;

import com.foodcourt.user.domain.model.Caller;
import com.foodcourt.user.domain.model.CustomerPhone;

/**
 * Inbound port for reading a customer's phone.
 * An administrator or an employee may read any customer. A customer may read only their own.
 */
public interface FindCustomerPhone {

    CustomerPhone find(Caller caller, long id);
}
