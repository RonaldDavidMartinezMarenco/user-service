package com.foodcourt.user.infrastructure.output.jpa.entity;

import java.time.LocalDate;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Row of {@code app_user}. Text columns use {@link SqlTypes#LONGVARCHAR} so they
 * match PostgreSQL {@code text}. {@code birth_date} stays nullable because only
 * an owner must have one. {@code restaurant_id} is set only for an employee.
 */
@Entity
@Table(name = "app_user")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "identity_document", nullable = false, unique = true)
    private String identityDocument;

    @Column(name = "phone", nullable = false, length = 13)
    private String phone;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 72)
    private String passwordHash;

    @Column(name = "role_id", nullable = false)
    private short roleId;

    @Column(name = "restaurant_id")
    private Long restaurantId;

    protected UserEntity() {
    }

    public UserEntity(
            Long id,
            String firstName,
            String lastName,
            String identityDocument,
            String phone,
            LocalDate birthDate,
            String email,
            String passwordHash,
            short roleId,
            Long restaurantId) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.identityDocument = identityDocument;
        this.phone = phone;
        this.birthDate = birthDate;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roleId = roleId;
        this.restaurantId = restaurantId;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getIdentityDocument() {
        return identityDocument;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public short getRoleId() {
        return roleId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }
}
