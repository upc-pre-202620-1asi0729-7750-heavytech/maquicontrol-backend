package com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserAccountJpaRepository extends JpaRepository<UserAccountPersistenceEntity, Long> { Optional<UserAccountPersistenceEntity> findByEmailIgnoreCase(String email); boolean existsByEmailIgnoreCase(String email); }
