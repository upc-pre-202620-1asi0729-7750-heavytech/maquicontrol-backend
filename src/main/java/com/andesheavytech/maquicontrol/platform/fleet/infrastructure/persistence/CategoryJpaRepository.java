package com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface CategoryJpaRepository extends JpaRepository<CategoryPersistenceEntity, Long> { Optional<CategoryPersistenceEntity> findByCodeIgnoreCase(String code); List<CategoryPersistenceEntity> findAllByActiveTrueOrderByNameAsc(); }
