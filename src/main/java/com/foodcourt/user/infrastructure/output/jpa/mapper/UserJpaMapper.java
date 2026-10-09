package com.foodcourt.user.infrastructure.output.jpa.mapper;

import com.foodcourt.user.domain.model.Role;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.infrastructure.output.jpa.entity.UserEntity;

/** Copies fields between {@link UserEntity} and {@link User}. Applies no rule. */
public final class UserJpaMapper {

    public UserEntity toEntity(User user) {
        return new UserEntity(
                user.id(),
                user.firstName(),
                user.lastName(),
                user.identityDocument(),
                user.phone(),
                user.birthDate(),
                user.email(),
                user.passwordHash(),
                user.role().id(),
                user.restaurantId());
    }

    public User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getIdentityDocument(),
                entity.getPhone(),
                entity.getBirthDate(),
                entity.getEmail(),
                entity.getPasswordHash(),
                Role.fromId(entity.getRoleId()),
                entity.getRestaurantId());
    }
}
