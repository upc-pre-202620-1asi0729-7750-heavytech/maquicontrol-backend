package com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence;

import com.andesheavytech.maquicontrol.platform.iam.domain.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface RoleJpaRepository extends JpaRepository<RolePersistenceEntity, Long> { Optional<RolePersistenceEntity> findByName(RoleName name); }
