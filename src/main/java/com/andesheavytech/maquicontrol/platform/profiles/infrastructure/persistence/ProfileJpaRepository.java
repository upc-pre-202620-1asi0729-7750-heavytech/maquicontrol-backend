package com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ProfileJpaRepository extends JpaRepository<ProfilePersistenceEntity, Long> { Optional<ProfilePersistenceEntity> findByUserAccountId(Long userAccountId); }
