package com.andesheavytech.maquicontrol.iam.infrastructure.persistence.jpa.entities;

import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@Table(name = "credentials")
@NoArgsConstructor
public class CredentialPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @OneToOne(optional = false)
    @JoinColumn(
            name = "user_account_id",
            referencedColumnName = "id",
            nullable = false,
            unique = true
    )
    private UserAccountPersistenceEntity userAccount;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    public CredentialPersistenceEntity(
            UserAccountPersistenceEntity userAccount,
            String passwordHash,
            Instant changedAt
    ) {
        this.userAccount = userAccount;
        this.passwordHash = passwordHash;
        this.changedAt = changedAt;
    }

    public void updateFrom(CredentialPersistenceEntity updated) {
        this.passwordHash = updated.passwordHash;
        this.changedAt = updated.changedAt;
    }
}