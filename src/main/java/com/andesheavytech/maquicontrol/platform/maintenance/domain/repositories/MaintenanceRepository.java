package com.andesheavytech.maquicontrol.platform.maintenance.domain.repositories;

import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.aggregates.Maintenance;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.MaintenanceStatus;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MaintenanceRepository {

    Maintenance save(Maintenance maintenance);

    Optional<Maintenance> findById(Long id);

    List<Maintenance> findByMachineryId(Long machineryId);

    List<Maintenance> findByMachineryIds(Collection<Long> machineryIds);

    List<Maintenance> findByStatus(MaintenanceStatus status);

    List<Maintenance> findCompletedByMachineryId(Long machineryId);

    List<Maintenance> findPendingByMachineryIds(Collection<Long> machineryIds);

    Optional<Maintenance> findPendingCorrectiveByMachineryId(Long machineryId);
}
