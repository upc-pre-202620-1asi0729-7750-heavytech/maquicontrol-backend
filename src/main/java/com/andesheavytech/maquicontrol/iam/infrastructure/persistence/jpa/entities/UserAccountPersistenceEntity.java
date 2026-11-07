package com.andesheavytech.maquicontrol.iam.infrastructure.persistence.jpa.entities;

import com.andesheavytech.maquicontrol.iam.domain.AccountStatus;
import com.andesheavytech.maquicontrol.iam.domain.RoleName;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "user_accounts",
        indexes = @Index(
                name = "idx_user_accounts_account_id",
                columnList = "account_id",
                unique = true
        )
)
@NoArgsConstructor
public class UserAccountPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "account_id", nullable = false, unique = true, updatable = false)
    private UUID accountId;

    @Column(nullable = false, unique = true, length = 320)
    private String email;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private AccountStatus status;

    private Instant lastLoginAt;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "user_account_roles",
            joinColumns = @JoinColumn(name = "user_account_id")
    )
    @Column(name = "role_name", nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private Set<RoleName> roles = new HashSet<>();

    public UserAccountPersistenceEntity(
            UUID accountId,
            String email,
            AccountStatus status,
            Instant lastLoginAt,
            Set<RoleName> roles
    ) {
        this.accountId = accountId;
        this.email = email;
        this.status = status;
        this.lastLoginAt = lastLoginAt;
        this.roles = new HashSet<>(roles);
    }

    public void updateFrom(UserAccountPersistenceEntity updated) {
        this.email = updated.email;
        this.status = updated.status;
        this.lastLoginAt = updated.lastLoginAt;
        this.roles.clear();
        this.roles.addAll(updated.roles);
    }
}