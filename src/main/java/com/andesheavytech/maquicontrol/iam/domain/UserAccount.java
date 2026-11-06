package com.andesheavytech.maquicontrol.iam.domain;

import com.andesheavytech.maquicontrol.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

public class UserAccount extends AbstractDomainAggregateRoot<UserAccount> {

    private final UUID id;
    private final String email;
    private final Credential credential;
    private AccountStatus status;
    private final Set<RoleName> roles;
    private final Instant createdAt;
    private Instant lastLoginAt;

    public UserAccount(UUID id, String email, String passwordHash) {
        if (id == null) {
            throw new IllegalArgumentException("Account ID is required");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }

        this.id = id;
        this.email = email.trim().toLowerCase();
        this.credential = new Credential(passwordHash);
        this.status = AccountStatus.PENDING;
        this.roles = EnumSet.of(RoleName.CONTRACTOR);
        this.createdAt = Instant.now();
    }

    public void activate() {
        this.status = AccountStatus.ACTIVE;
    }

    public void suspend() {
        this.status = AccountStatus.SUSPENDED;
    }

    public void assignRole(RoleName role) {
        if (role == null) {
            throw new IllegalArgumentException("Role is required");
        }
        roles.add(role);
    }

    public void changePassword(String newPasswordHash) {
        credential.update(newPasswordHash);
    }

    public void recordSuccessfulLogin() {
        if (status != AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }
        lastLoginAt = Instant.now();
    }

    public UUID getId() { return id; }
    public String getEmail() { return email; }
    public Credential getCredential() { return credential; }
    public AccountStatus getStatus() { return status; }
    public Set<RoleName> getRoles() { return Set.copyOf(roles); }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getLastLoginAt() { return lastLoginAt; }
}