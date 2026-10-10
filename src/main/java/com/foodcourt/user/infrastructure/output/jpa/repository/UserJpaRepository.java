package com.foodcourt.user.infrastructure.output.jpa.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodcourt.user.infrastructure.output.jpa.entity.UserEntity;

/** Spring Data access to {@code app_user}. Used only by the JPA adapter. */
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByRoleId(short roleId);

    boolean existsByEmail(String email);

    boolean existsByIdentityDocument(String identityDocument);
}
