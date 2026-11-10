package com.andesheavytech.maquicontrol.platform.maintenance.application.internal.queryservices;

import com.andesheavytech.maquicontrol.platform.maintenance.application.internal.outboundservices.acl.ExternalFleetService;
import com.andesheavytech.maquicontrol.platform.maintenance.application.queryservices.MaintenanceManagementQueryService;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.aggregates.Maintenance;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.queries.CheckMaintenanceDetailsQuery;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.queries.GetMaintenancesByOwnerProfileIdQuery;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.queries.ViewBreakdownReportsQuery;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.queries.ViewMaintenanceHistoryQuery;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.queries.ViewMaintenanceScheduleQuery;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.repositories.MaintenanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link MaintenanceManagementQueryService}.
 */
@Service
public class MaintenanceManagementQueryServiceImpl implements MaintenanceManagementQueryService {

    private final MaintenanceRepository maintenanceRepository;
    private final ExternalFleetService externalFleetService;

    public MaintenanceManagementQueryServiceImpl(MaintenanceRepository maintenanceRepository,
                                                 ExternalFleetService externalFleetService) {
        this.maintenanceRepository = maintenanceRepository;
        this.externalFleetService = externalFleetService;
    }

    @Override
    public Optional<Maintenance> handle(CheckMaintenanceDetailsQuery query) {
        return maintenanceRepository.findById(query.maintenanceId());
    }

    @Override
    public List<Maintenance> handle(ViewMaintenanceHistoryQuery query) {
        return maintenanceRepository.findCompletedByMachineryId(query.machineryId());
    }

    @Override
    public List<Maintenance> handle(ViewMaintenanceScheduleQuery query) {
        var machineryIds = externalFleetService.fetchMachineryIdsByOwnerProfileId(query.ownerProfileId());
        return machineryIds.isEmpty() ? List.of() : maintenanceRepository.findPendingByMachineryIds(machineryIds);
    }

    @Override
    public List<Maintenance> handle(GetMaintenancesByOwnerProfileIdQuery query) {
        var machineryIds = externalFleetService.fetchMachineryIdsByOwnerProfileId(query.ownerProfileId());
        if (query.machineryId() != null) {
            return machineryIds.contains(query.machineryId())
                    ? maintenanceRepository.findByMachineryId(query.machineryId())
                    : List.of();
        }
        return machineryIds.isEmpty() ? List.of() : maintenanceRepository.findByMachineryIds(machineryIds);
    }

    @Override
    public List<Maintenance> handle(ViewBreakdownReportsQuery query) {
        return maintenanceRepository.findByMachineryId(query.machineryId()).stream()
                .filter(maintenance -> !maintenance.getBreakdownReports().isEmpty())
                .toList();
    }
}
