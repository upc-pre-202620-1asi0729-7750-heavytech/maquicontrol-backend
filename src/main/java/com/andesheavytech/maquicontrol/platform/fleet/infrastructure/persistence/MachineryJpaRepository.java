package com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence;
import com.andesheavytech.maquicontrol.platform.fleet.domain.MachineryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface MachineryJpaRepository extends JpaRepository<MachineryPersistenceEntity, Long> { List<MachineryPersistenceEntity> findAllByCategoryId(Long categoryId); List<MachineryPersistenceEntity> findAllByStatus(MachineryStatus status); List<MachineryPersistenceEntity> findAllByDistrictContainingIgnoreCase(String district); }
