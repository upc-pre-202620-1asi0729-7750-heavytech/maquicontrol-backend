package com.andesheavytech.maquicontrol.iam.infrastructure.persistence.jpa.assemblers;

import com.andesheavytech.maquicontrol.iam.domain.UserAccount;
import com.andesheavytech.maquicontrol.iam.infrastructure.persistence.jpa.entities.CredentialPersistenceEntity;
import com.andesheavytech.maquicontrol.iam.infrastructure.persistence.jpa.entities.UserAccountPersistenceEntity;

public class UserAccountPersistenceAssembler {

    public UserAccountPersistenceEntity toPersistenceEntity(UserAccount account) {
        return new UserAccountPersistenceEntity(
                account.getId(),
                account.getEmail(),
                account.getStatus(),
                account.getLastLoginAt(),
                account.getRoles()
        );
    }

    public CredentialPersistenceEntity toCredentialPersistenceEntity(
            UserAccountPersistenceEntity accountEntity,
            UserAccount account
    ) {
        return new CredentialPersistenceEntity(
                accountEntity,
                account.getCredential().getPasswordHash(),
                account.getCredential().getChangedAt()
        );
    }

    public UserAccount toDomain(
            UserAccountPersistenceEntity accountEntity,
            CredentialPersistenceEntity credentialEntity
    ) {
        return UserAccount.restore(
                accountEntity.getAccountId(),
                accountEntity.getEmail(),
                credentialEntity.getPasswordHash(),
                credentialEntity.getChangedAt(),
                accountEntity.getStatus(),
                accountEntity.getRoles(),
                accountEntity.getCreatedAt().toInstant(),
                accountEntity.getLastLoginAt()
        );
    }
}