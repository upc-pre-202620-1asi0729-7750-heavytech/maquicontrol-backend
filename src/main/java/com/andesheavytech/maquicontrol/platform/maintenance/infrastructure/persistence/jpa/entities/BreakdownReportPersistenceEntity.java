package com.andesheavytech.maquicontrol.platform.maintenance.infrastructure.persistence.jpa.entities;

import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.BreakdownSeverity;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * JPA entity for the {@code breakdown_reports} table.
 */
@Entity
@Table(name = "breakdown_reports")
@Getter
@Setter
@NoArgsConstructor
public class BreakdownReportPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 20)
    private BreakdownSeverity severity;

    @Column(name = "reported_at", nullable = false)
    private LocalDateTime reportedAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolved", nullable = false)
    private boolean resolved;
}
