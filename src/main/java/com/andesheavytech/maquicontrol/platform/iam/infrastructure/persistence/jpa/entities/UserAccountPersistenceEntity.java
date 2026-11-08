package com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence.jpa.entities;

import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.AccountStatus;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * JPA entity for the {@code user_accounts} table. The credential lives in the {@code credentials}
 * table, joined one to one by {@code user_account_id}.
 */
@Entity
@Table(name = "user_accounts")
@SecondaryTable(name = "credentials", pkJoinColumns = @PrimaryKeyJoinColumn(name = "user_account_id"))
@Getter
@Setter
@NoArgsConstructor
public class UserAccountPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "email", nullable = false, unique = true, length = 254)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private AccountStatus status;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(table = "credentials", name = "password_hash", nullable = false, length = 120)
    private String passwordHash;

    @Column(table = "credentials", name = "changed_at", nullable = false)
    private LocalDateTime passwordChangedAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_account_roles",
            joinColumns = @JoinColumn(name = "user_account_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<RolePersistenceEntity> roles = new HashSet<>();
}
