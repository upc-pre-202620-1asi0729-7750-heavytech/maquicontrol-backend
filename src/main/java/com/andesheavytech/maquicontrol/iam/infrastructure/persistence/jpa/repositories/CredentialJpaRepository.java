package com.andesheavytech.maquicontrol.iam.infrastructure.persistence.jpa.repositories;

import com.andesheavytech.maquicontrol.iam.infrastructure.persistence.jpa.entities.CredentialPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface CredentialJpaRepository
        extends JpaRepository<CredentialPersistenceEntity, Long> {
    Optional<CredentialPersistenceEntity> findByUserAccount_AccountId(UUID accountId);
}