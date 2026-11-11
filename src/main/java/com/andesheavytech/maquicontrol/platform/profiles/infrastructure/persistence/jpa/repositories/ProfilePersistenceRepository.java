package com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence.jpa.repositories;

import com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfilePersistenceRepository extends JpaRepository<ProfilePersistenceEntity, Long> {

    Optional<ProfilePersistenceEntity> findByUserAccountId(Long userAccountId);

    Optional<ProfilePersistenceEntity> findFirstByOrganizationId(Long organizationId);

    boolean existsByUserAccountId(Long userAccountId);

    boolean existsByDocumentNumber(String documentNumber);

    boolean existsByDocumentNumberAndIdNot(String documentNumber, Long id);
}
