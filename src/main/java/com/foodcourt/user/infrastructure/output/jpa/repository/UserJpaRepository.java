package com.foodcourt.user.infrastructure.output.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodcourt.user.infrastructure.output.jpa.entity.UserEntity;

/** Spring Data access to {@code app_user}. Used only by the JPA adapter. */
public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);

    boolean existsByIdentityDocument(String identityDocument);
}
