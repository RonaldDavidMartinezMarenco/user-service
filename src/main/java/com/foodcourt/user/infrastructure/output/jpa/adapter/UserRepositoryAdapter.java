package com.foodcourt.user.infrastructure.output.jpa.adapter;

import java.sql.SQLException;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodcourt.user.domain.exception.EmailAlreadyRegistered;
import com.foodcourt.user.domain.exception.IdentityDocumentAlreadyRegistered;
import com.foodcourt.user.domain.model.User;
import com.foodcourt.user.domain.spi.UserRepository;
import com.foodcourt.user.infrastructure.output.jpa.entity.UserEntity;
import com.foodcourt.user.infrastructure.output.jpa.mapper.UserJpaMapper;
import com.foodcourt.user.infrastructure.output.jpa.repository.UserJpaRepository;

/**
 * JPA adapter for {@link UserRepository}.
 * A concurrent insert can pass the exists checks and still hit the unique index.
 * PostgreSQL reports that as SQLState {@code 23505}; the constraint name distinguishes
 * {@code app_user_email_key} from {@code app_user_identity_document_key}.
 * The class is not final: {@code @Transactional} needs a CGLIB subclass.
 */
@Service
public class UserRepositoryAdapter implements UserRepository {

    /** PostgreSQL {@code unique_violation}. */
    static final String UNIQUE_VIOLATION = "23505";

    private final UserJpaRepository accounts;
    private final UserJpaMapper mapper = new UserJpaMapper();

    public UserRepositoryAdapter(UserJpaRepository accounts) {
        this.accounts = accounts;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(long id) {
        return accounts.findById(id).map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return accounts.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByIdentityDocument(String identityDocument) {
        return accounts.existsByIdentityDocument(identityDocument);
    }

    @Override
    @Transactional
    public User save(User user) {
        try {
            UserEntity stored = accounts.saveAndFlush(mapper.toEntity(user));
            return mapper.toDomain(stored);
        } catch (DataIntegrityViolationException failure) {
            throw translated(failure);
        }
    }

    private RuntimeException translated(DataIntegrityViolationException failure) {
        if (isUniqueViolation(failure)) {
            return domainDuplicate(causeMessage(failure), failure);
        }
        return failure;
    }

    private static RuntimeException domainDuplicate(String message, DataIntegrityViolationException failure) {
        if (mentionsIdentityDocument(message)) {
            return new IdentityDocumentAlreadyRegistered();
        }
        return emailDuplicateOrOriginal(message, failure);
    }

    private static RuntimeException emailDuplicateOrOriginal(
            String message,
            DataIntegrityViolationException failure) {
        if (mentionsEmail(message)) {
            return new EmailAlreadyRegistered();
        }
        return failure;
    }

    private static boolean isUniqueViolation(DataIntegrityViolationException failure) {
        Throwable cause = failure.getMostSpecificCause();
        if (cause instanceof SQLException sqlException) {
            return UNIQUE_VIOLATION.equals(sqlException.getSQLState());
        }
        return false;
    }

    private static String causeMessage(DataIntegrityViolationException failure) {
        return failure.getMostSpecificCause().getMessage();
    }

    private static boolean mentionsIdentityDocument(String message) {
        return hasText(message) && message.contains("identity_document");
    }

    private static boolean mentionsEmail(String message) {
        return hasText(message) && message.contains("email");
    }

    private static boolean hasText(String message) {
        return message != null;
    }
}
