package com.foodcourt.user.application.handler;

import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.api.CreateEmployee;
import com.foodcourt.user.domain.model.NewEmployee;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.CurrentCaller;

/**
 * Entry the controller calls. Validation stays on the request DTO and the
 * rules stay in {@link CreateEmployee}. This type reads the caller and forwards the call.
 */
@Service
public final class CreateEmployeeHandler {

    private final CreateEmployee createEmployee;
    private final CurrentCaller currentCaller;

    public CreateEmployeeHandler(CreateEmployee createEmployee, CurrentCaller currentCaller) {
        this.createEmployee = createEmployee;
        this.currentCaller = currentCaller;
    }

    public User create(NewEmployee employee) {
        return createEmployee.create(currentCaller.require(), employee);
    }
}
