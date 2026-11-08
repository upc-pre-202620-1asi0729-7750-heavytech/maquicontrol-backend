package com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.aggregates.Maintenance;
import com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.resources.BreakdownReportResource;
import com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.resources.MaintenanceCostResource;
import com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.resources.MaintenanceResource;

public final class MaintenanceResourceFromEntityAssembler {

    private MaintenanceResourceFromEntityAssembler() {
    }

    public static MaintenanceResource toResourceFromEntity(Maintenance maintenance) {
        var reports = maintenance.getBreakdownReports().stream()
                .map(report -> new BreakdownReportResource(report.getId(), report.getDescription(),
                        report.getSeverity().name(), report.getReportedAt(), report.getResolvedAt(), report.isResolved()))
                .toList();
        var cost = maintenance.getCost() == null
                ? null
                : new MaintenanceCostResource(maintenance.getCost().amount(), maintenance.getCost().currency().name());
        return new MaintenanceResource(maintenance.getId(), maintenance.getMachineryId(), maintenance.getType().name(),
                maintenance.getStatus().name(), maintenance.getDescription(), maintenance.getScheduledDate(),
                maintenance.getStartedAt(), maintenance.getCompletedAt(), maintenance.getTechnicianName(), cost, reports);
    }
}
