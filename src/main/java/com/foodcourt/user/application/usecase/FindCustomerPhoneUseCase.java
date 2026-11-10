package com.foodcourt.user.application.usecase;

import org.springframework.stereotype.Service;

import com.foodcourt.user.domain.api.FindCustomerPhone;
import com.foodcourt.user.domain.exception.AccessDenied;
import com.foodcourt.user.domain.exception.UserNotFound;
import com.foodcourt.user.domain.model.Caller;
import com.foodcourt.user.domain.model.CustomerPhone;
import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.UserRepository;

/**
 * Reads the phone of a customer. An administrator or an employee may read any customer.
 * Any other caller may read only their own phone, and only when that account is a customer.
 * A missing row and an account that is not a customer share {@link UserNotFound}, so the role is not revealed.
 */
@Service
public final class FindCustomerPhoneUseCase implements FindCustomerPhone {

    private final UserRepository users;

    public FindCustomerPhoneUseCase(UserRepository users) {
        this.users = users;
    }

    @Override
    public CustomerPhone find(Caller caller, long id) {
        requireAllowed(caller, id);
        return phone(users.findById(id).orElseThrow(UserNotFound::new));
    }

    private static void requireAllowed(Caller caller, long id) {
        if (!allowed(caller, id)) {
            throw new AccessDenied();
        }
    }

    private static boolean allowed(Caller caller, long id) {
        return caller.role() == Role.ADMINISTRATOR || caller.role() == Role.EMPLOYEE || caller.id() == id;
    }

    private static CustomerPhone phone(User user) {
        if (user.role() != Role.CUSTOMER) {
            throw new UserNotFound();
        }
        return new CustomerPhone(user.phone());
    }
}
