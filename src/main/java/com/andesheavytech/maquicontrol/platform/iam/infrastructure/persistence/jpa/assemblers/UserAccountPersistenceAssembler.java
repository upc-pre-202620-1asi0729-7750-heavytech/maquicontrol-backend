package com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence.jpa.assemblers;

import com.andesheavytech.maquicontrol.platform.iam.domain.model.aggregates.UserAccount;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.Credential;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.EmailAddress;
import com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence.jpa.entities.RolePersistenceEntity;
import com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence.jpa.entities.UserAccountPersistenceEntity;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Maps between {@link UserAccount} and {@link UserAccountPersistenceEntity}.
 */
public final class UserAccountPersistenceAssembler {

    private UserAccountPersistenceAssembler() {
    }

    public static UserAccount toDomainFromPersistence(UserAccountPersistenceEntity entity) {
        var roles = entity.getRoles().stream()
                .map(RolePersistenceAssembler::toDomainFromPersistence)
                .collect(Collectors.toSet());
        return UserAccount.reconstitute(
                entity.getId(),
                new EmailAddress(entity.getEmail()),
                new Credential(entity.getPasswordHash(), entity.getPasswordChangedAt()),
                entity.getStatus(),
                roles,
                entity.getLastLoginAt());
    }

    /**
     * Builds the persistence entity. Roles are passed already loaded, so the join table points to
     * existing rows instead of new ones.
     */
    public static UserAccountPersistenceEntity toPersistenceFromDomain(UserAccount account,
                                                                       Set<RolePersistenceEntity> roles) {
        var entity = new UserAccountPersistenceEntity();
        if (account.getId() != null) {
            entity.setId(account.getId());
        }
        entity.setEmail(account.getEmailValue());
        entity.setStatus(account.getStatus());
        entity.setLastLoginAt(account.getLastLoginAt());
        entity.setPasswordHash(account.getCredential().passwordHash());
        entity.setPasswordChangedAt(account.getCredential().changedAt());
        entity.setRoles(roles);
        return entity;
    }
}
