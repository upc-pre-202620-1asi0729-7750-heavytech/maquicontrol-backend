package com.andesheavytech.maquicontrol.platform.maintenance.domain.model.aggregates;

import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.entities.BreakdownReport;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.events.BreakdownReportedEvent;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.events.MaintenanceCompletedEvent;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.events.MaintenanceScheduledEvent;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.events.MaintenanceStatusUpdatedEvent;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.MaintenanceStatus;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.MaintenanceType;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Maintenance aggregate root: a preventive, corrective or inspection job on a machine, with the
 * breakdowns it repairs.
 */
@Getter
public class Maintenance extends AbstractDomainAggregateRoot<Maintenance> {

    private final Long id;
    private final Long machineryId;
    private final MaintenanceType type;
    private MaintenanceStatus status;
    private String description;
    private LocalDateTime scheduledDate;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String technicianName;
    private Money cost;
    private final List<BreakdownReport> breakdownReports;

    private Maintenance(Long id, Long machineryId, MaintenanceType type, MaintenanceStatus status, String description,
                        LocalDateTime scheduledDate, LocalDateTime startedAt, LocalDateTime completedAt,
                        String technicianName, Money cost, List<BreakdownReport> breakdownReports) {
        if (machineryId == null || machineryId <= 0) {
            throw new IllegalArgumentException("machineryId is required");
        }
        this.id = id;
        this.machineryId = machineryId;
        this.type = Objects.requireNonNull(type, "type is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.description = requireDescription(description);
        this.scheduledDate = Objects.requireNonNull(scheduledDate, "scheduledDate is required");
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.technicianName = normalizeTechnician(technicianName);
        this.cost = cost;
        this.breakdownReports = new ArrayList<>(breakdownReports == null ? List.of() : breakdownReports);
    }

    /**
     * Schedules a maintenance. The date cannot be in the past.
     *
     * @param now the current time
     */
    public static Maintenance schedule(Long machineryId, MaintenanceType type, String description,
                                       LocalDateTime scheduledDate, String technicianName, LocalDateTime now) {
        Objects.requireNonNull(scheduledDate, "scheduledDate is required");
        if (scheduledDate.toLocalDate().isBefore(now.toLocalDate())) {
            throw new IllegalArgumentException("scheduledDate cannot be in the past");
        }
        return new Maintenance(null, machineryId, type, MaintenanceStatus.SCHEDULED, description, scheduledDate,
                null, null, technicianName, null, List.of());
    }

    /**
     * Policy "when a breakdown is reported, schedule corrective maintenance": creates a corrective
     * maintenance for today that already contains the report.
     */
    public static Maintenance scheduleCorrective(Long machineryId, BreakdownReport report, LocalDateTime now) {
        Objects.requireNonNull(report, "report is required");
        var maintenance = new Maintenance(null, machineryId, MaintenanceType.CORRECTIVE, MaintenanceStatus.SCHEDULED,
                "Corrective maintenance: " + report.getDescription(), now, null, null, null, null, List.of(report));
        return maintenance;
    }

    public static Maintenance reconstitute(Long id, Long machineryId, MaintenanceType type, MaintenanceStatus status,
                                           String description, LocalDateTime scheduledDate, LocalDateTime startedAt,
                                           LocalDateTime completedAt, String technicianName, Money cost,
                                           List<BreakdownReport> breakdownReports) {
        return new Maintenance(id, machineryId, type, status, description, scheduledDate, startedAt, completedAt,
                technicianName, cost, breakdownReports);
    }

    /**
     * Registers the scheduling events. Called by the repository adapter once the id is assigned.
     */
    public void onScheduled() {
        registerDomainEvent(new MaintenanceScheduledEvent(id, machineryId, type.name()));
        breakdownReports.stream()
                .filter(report -> report.getId() == null)
                .forEach(report -> registerDomainEvent(new BreakdownReportedEvent(id, machineryId, report.getSeverity().name())));
    }

    /** The technician starts working on the machine. */
    public void start() {
        requireStatus(MaintenanceStatus.SCHEDULED, "start");
        this.status = MaintenanceStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
        registerDomainEvent(new MaintenanceStatusUpdatedEvent(id, machineryId, status.name()));
    }

    /**
     * Finishes the maintenance, records its cost and resolves its breakdown reports.
     *
     * @param cost the cost, or {@code null} when it is not known
     */
    public void complete(Money cost) {
        requireStatus(MaintenanceStatus.IN_PROGRESS, "complete");
        this.status = MaintenanceStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.cost = cost;
        breakdownReports.forEach(BreakdownReport::resolve);
        registerDomainEvent(new MaintenanceCompletedEvent(id, machineryId));
    }

    /**
     * Status change requested by the owner: IN_PROGRESS starts it, CANCELLED cancels it while it
     * is still scheduled, and COMPLETED completes it without a cost.
     */
    public void updateStatus(MaintenanceStatus newStatus) {
        Objects.requireNonNull(newStatus, "status is required");
        switch (newStatus) {
            case IN_PROGRESS -> start();
            case COMPLETED -> complete(null);
            case CANCELLED -> {
                requireStatus(MaintenanceStatus.SCHEDULED, "cancel");
                this.status = MaintenanceStatus.CANCELLED;
                registerDomainEvent(new MaintenanceStatusUpdatedEvent(id, machineryId, status.name()));
            }
            case SCHEDULED -> throw new IllegalArgumentException("a maintenance cannot go back to SCHEDULED");
        }
    }

    /**
     * Adds a breakdown to a pending maintenance.
     */
    public void reportBreakdown(BreakdownReport report) {
        Objects.requireNonNull(report, "report is required");
        if (!isPending()) {
            throw new IllegalStateException("cannot report a breakdown on a maintenance that is %s".formatted(status));
        }
        breakdownReports.add(report);
        registerDomainEvent(new BreakdownReportedEvent(id, machineryId, report.getSeverity().name()));
    }

    public boolean isPending() {
        return status.isPending();
    }

    public boolean hasCriticalBreakdown() {
        return breakdownReports.stream().anyMatch(report -> !report.isResolved() && report.isCritical());
    }

    public List<BreakdownReport> getBreakdownReports() {
        return Collections.unmodifiableList(breakdownReports);
    }

    private void requireStatus(MaintenanceStatus expected, String action) {
        if (status != expected) {
            throw new IllegalStateException("cannot %s a maintenance that is %s".formatted(action, status));
        }
    }

    private static String requireDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("description is required");
        }
        var trimmed = description.trim();
        return trimmed.length() > 500 ? trimmed.substring(0, 500) : trimmed;
    }

    private static String normalizeTechnician(String technicianName) {
        if (technicianName == null || technicianName.isBlank()) {
            return null;
        }
        var trimmed = technicianName.trim();
        if (trimmed.length() > 150) {
            throw new IllegalArgumentException("technicianName must have at most 150 characters");
        }
        return trimmed;
    }
}
