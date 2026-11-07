package com.andesheavytech.maquicontrol.iam.domain;

import java.time.Instant;
import java.util.Objects;

public class Credential {

    private String passwordHash;
    private Instant changedAt;

    public Credential(String passwordHash) {
        this(passwordHash, Instant.now());
    }

    public Credential(String passwordHash, Instant changedAt) {
        if (passwordHash == null || passwordHash.isBlank()) {
            throw new IllegalArgumentException("Password hash cannot be empty");
        }
        if (changedAt == null) {
            throw new IllegalArgumentException("Credential change date is required");
        }

        this.passwordHash = passwordHash;
        this.changedAt = changedAt;
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