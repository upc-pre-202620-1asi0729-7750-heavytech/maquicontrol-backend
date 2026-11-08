package com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Hashed credential owned by a user account.
 *
 * @param passwordHash the BCrypt hash of the password
 * @param changedAt    when the password was last set
 */
public record Credential(String passwordHash, LocalDateTime changedAt) {

    public Credential {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("passwordHash is required");
        }
        Objects.requireNonNull(changedAt, "changedAt is required");
    }

    public static Credential of(String passwordHash) {
        return new Credential(passwordHash, LocalDateTime.now());
    }

    /**
     * Returns a new credential with the given hash.
     */
    public Credential update(String newPasswordHash) {
        return Credential.of(newPasswordHash);
    }
}
