package com.andesheavytech.maquicontrol.iam.infrastructure.persistence.jpa.repositories;

import com.andesheavytech.maquicontrol.iam.infrastructure.persistence.jpa.entities.CredentialPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CredentialJpaRepository
        extends JpaRepository<CredentialPersistenceEntity, Long> {
}