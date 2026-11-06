package com.andesheavytech.maquicontrol.iam.infrastructure.persistence.jpa.repositories;

import com.andesheavytech.maquicontrol.iam.infrastructure.persistence.jpa.entities.UserAccountPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountJpaRepository
        extends JpaRepository<UserAccountPersistenceEntity, Long> {

    boolean existsByEmail(String email);

    Optional<UserAccountPersistenceEntity> findByEmail(String email);
}