package com.andesheavytech.maquicontrol.platform.maintenance.infrastructure.persistence.jpa.repositories;

import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.MaintenanceStatus;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.MaintenanceType;
import com.andesheavytech.maquicontrol.platform.maintenance.infrastructure.persistence.jpa.entities.MaintenancePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaintenancePersistenceRepository extends JpaRepository<MaintenancePersistenceEntity, Long> {

    List<MaintenancePersistenceEntity> findByMachineryIdOrderByScheduledDateDesc(Long machineryId);

    List<MaintenancePersistenceEntity> findByMachineryIdInOrderByScheduledDateDesc(Collection<Long> machineryIds);

    List<MaintenancePersistenceEntity> findByStatusOrderByScheduledDateAsc(MaintenanceStatus status);

    List<MaintenancePersistenceEntity> findByMachineryIdAndStatusOrderByCompletedAtDesc(Long machineryId, MaintenanceStatus status);

    List<MaintenancePersistenceEntity> findByMachineryIdInAndStatusInOrderByScheduledDateAsc(Collection<Long> machineryIds,
                                                                                            Collection<MaintenanceStatus> statuses);

    Optional<MaintenancePersistenceEntity> findFirstByMachineryIdAndTypeAndStatusInOrderByIdDesc(
            Long machineryId, MaintenanceType type, Collection<MaintenanceStatus> statuses);
}
