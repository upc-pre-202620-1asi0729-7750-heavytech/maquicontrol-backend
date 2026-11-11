package com.andesheavytech.maquicontrol.platform.maintenance.infrastructure.persistence.jpa.assemblers;

import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.aggregates.Maintenance;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.entities.BreakdownReport;
import com.andesheavytech.maquicontrol.platform.maintenance.infrastructure.persistence.jpa.entities.BreakdownReportPersistenceEntity;
import com.andesheavytech.maquicontrol.platform.maintenance.infrastructure.persistence.jpa.entities.MaintenancePersistenceEntity;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;

import java.util.HashMap;
import java.util.Map;

public final class MaintenancePersistenceAssembler {

    private MaintenancePersistenceAssembler() {
    }

    public static Maintenance toDomainFromPersistence(MaintenancePersistenceEntity entity) {
        var reports = entity.getBreakdownReports().stream()
                .map(report -> new BreakdownReport(report.getId(), report.getDescription(), report.getSeverity(),
                        report.getReportedAt(), report.getResolvedAt(), report.isResolved()))
                .toList();
        var cost = entity.getCostAmount() == null || entity.getCostCurrency() == null
                ? null
                : new Money(entity.getCostAmount(), entity.getCostCurrency());
        return Maintenance.reconstitute(entity.getId(), entity.getMachineryId(), entity.getType(), entity.getStatus(),
                entity.getDescription(), entity.getScheduledDate(), entity.getStartedAt(), entity.getCompletedAt(),
                entity.getTechnicianName(), cost, reports);
    }

    /**
     * Copies the aggregate into the entity. Existing breakdown reports are updated by id and new
     * ones are appended, so their rows keep their identity.
     */
    public static MaintenancePersistenceEntity copyToPersistence(Maintenance maintenance, MaintenancePersistenceEntity entity) {
        entity.setMachineryId(maintenance.getMachineryId());
        entity.setType(maintenance.getType());
        entity.setStatus(maintenance.getStatus());
        entity.setDescription(maintenance.getDescription());
        entity.setScheduledDate(maintenance.getScheduledDate());
        entity.setStartedAt(maintenance.getStartedAt());
        entity.setCompletedAt(maintenance.getCompletedAt());
        entity.setTechnicianName(maintenance.getTechnicianName());
        entity.setCostAmount(maintenance.getCost() == null ? null : maintenance.getCost().amount());
        entity.setCostCurrency(maintenance.getCost() == null ? null : maintenance.getCost().currency());
        Map<Long, BreakdownReportPersistenceEntity> existing = new HashMap<>();
        entity.getBreakdownReports().forEach(report -> existing.put(report.getId(), report));
        for (var report : maintenance.getBreakdownReports()) {
            var target = report.getId() == null ? null : existing.get(report.getId());
            if (target == null) {
                target = new BreakdownReportPersistenceEntity();
                entity.getBreakdownReports().add(target);
            }
            target.setDescription(report.getDescription());
            target.setSeverity(report.getSeverity());
            target.setReportedAt(report.getReportedAt());
            target.setResolvedAt(report.getResolvedAt());
            target.setResolved(report.isResolved());
        }
        return entity;
    }
}
