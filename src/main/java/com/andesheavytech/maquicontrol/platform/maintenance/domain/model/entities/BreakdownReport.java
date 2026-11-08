package com.andesheavytech.maquicontrol.platform.maintenance.domain.model.entities;

import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.BreakdownSeverity;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Breakdown reported on a machine. It belongs to the {@code Maintenance} that repairs it.
 */
@Getter
public class BreakdownReport {

    private final Long id;
    private final String description;
    private final BreakdownSeverity severity;
    private final LocalDateTime reportedAt;
    private LocalDateTime resolvedAt;
    private boolean resolved;

    public BreakdownReport(Long id, String description, BreakdownSeverity severity, LocalDateTime reportedAt,
                           LocalDateTime resolvedAt, boolean resolved) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("breakdown description is required");
        }
        if (description.trim().length() > 500) {
            throw new IllegalArgumentException("breakdown description must have at most 500 characters");
        }
        this.id = id;
        this.description = description.trim();
        this.severity = Objects.requireNonNull(severity, "severity is required");
        this.reportedAt = Objects.requireNonNull(reportedAt, "reportedAt is required");
        this.resolvedAt = resolvedAt;
        this.resolved = resolved;
    }

    /**
     * Creates a new, unresolved report.
     */
    public static BreakdownReport report(String description, BreakdownSeverity severity) {
        return new BreakdownReport(null, description, severity, LocalDateTime.now(), null, false);
    }

    public void resolve() {
        if (!resolved) {
            this.resolved = true;
            this.resolvedAt = LocalDateTime.now();
        }
    }

    public boolean isCritical() {
        return severity == BreakdownSeverity.CRITICAL;
    }
}
