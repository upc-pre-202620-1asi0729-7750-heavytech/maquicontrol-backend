package com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.jpa.repositories;

import com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.jpa.entities.CategoryPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryPersistenceRepository extends JpaRepository<CategoryPersistenceEntity, Long> {

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);
}
