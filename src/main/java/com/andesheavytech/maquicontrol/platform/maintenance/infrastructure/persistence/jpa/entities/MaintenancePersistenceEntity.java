package com.andesheavytech.maquicontrol.platform.maintenance.infrastructure.persistence.jpa.entities;

import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.MaintenanceStatus;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.MaintenanceType;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Currency;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity for the {@code maintenances} table. {@code machinery_id} refers to Fleet Management
 * without a database foreign key.
 */
@Entity
@Table(name = "maintenances")
@Getter
@Setter
@NoArgsConstructor
public class MaintenancePersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "machinery_id", nullable = false)
    private Long machineryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_type", nullable = false, length = 30)
    private MaintenanceType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private MaintenanceStatus status;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "scheduled_date", nullable = false)
    private LocalDateTime scheduledDate;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "technician_name", length = 150)
    private String technicianName;

    @Column(name = "cost_amount", precision = 12, scale = 2)
    private BigDecimal costAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "cost_currency", length = 3)
    private Currency costCurrency;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "maintenance_id", nullable = false)
    @OrderBy("id ASC")
    private List<BreakdownReportPersistenceEntity> breakdownReports = new ArrayList<>();
}
