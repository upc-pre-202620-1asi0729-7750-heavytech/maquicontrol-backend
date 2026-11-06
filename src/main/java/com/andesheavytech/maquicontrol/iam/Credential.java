package com.andesheavytech.maquicontrol.iam;

import java.time.Instant;
import java.util.Objects;

public class Credential {
    private String passwordHash;
    private Instant changedAt;

    public Credential(String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be empty");
        }

        this.passwordHash = passwordHash;
        this.changedAt = Instant.now();
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Instant getChangedAt() {
        return changedAt;
    }

    public void update(String newPasswordHash) {
        if (newPasswordHash == null || newPasswordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be empty");
        }

        this.passwordHash = newPasswordHash;
        this.changedAt = Instant.now();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof Credential credential)) return false;
        return Objects.equals(passwordHash, credential.passwordHash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(passwordHash);
    }
}