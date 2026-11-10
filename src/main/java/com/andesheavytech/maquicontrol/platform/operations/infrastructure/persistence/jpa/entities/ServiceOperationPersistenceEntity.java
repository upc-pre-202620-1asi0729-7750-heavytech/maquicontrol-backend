package com.andesheavytech.maquicontrol.platform.operations.infrastructure.persistence.jpa.entities;

import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.OperationStatus;
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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity for the {@code service_operations} table.
 */
@Entity
@Table(name = "service_operations")
@Getter
@Setter
@NoArgsConstructor
public class ServiceOperationPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "rental_id", nullable = false, unique = true)
    private Long rentalId;

    @Column(name = "machinery_id", nullable = false)
    private Long machineryId;

    @Column(name = "operator_profile_id", nullable = false)
    private Long operatorProfileId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private OperationStatus status;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "service_operation_id")
    @OrderBy("workDate ASC, startTime ASC")
    private List<WorkedHoursPersistenceEntity> workedHours = new ArrayList<>();
}
