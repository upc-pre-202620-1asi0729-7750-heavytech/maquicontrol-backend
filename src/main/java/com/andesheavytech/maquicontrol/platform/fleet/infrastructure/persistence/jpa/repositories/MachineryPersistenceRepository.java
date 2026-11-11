package com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.jpa.repositories;

import com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.jpa.entities.MachineryPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MachineryPersistenceRepository extends JpaRepository<MachineryPersistenceEntity, Long> {

    List<MachineryPersistenceEntity> findByCategoryId(Long categoryId);

    List<MachineryPersistenceEntity> findByOwnerProfileId(Long ownerProfileId);

    boolean existsByCategoryId(Long categoryId);
}
