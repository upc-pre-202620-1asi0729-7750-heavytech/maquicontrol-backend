package com.andesheavytech.maquicontrol.platform.operations.infrastructure.persistence.jpa.entities;

import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.WorkedHoursStatus;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * JPA entity for the {@code worked_hour_records} table.
 */
@Entity
@Table(name = "worked_hour_records")
@Getter
@Setter
@NoArgsConstructor
public class WorkedHoursPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(name = "work_date", nullable = false)
    private LocalDate workDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "total_hours", nullable = false, precision = 6, scale = 2)
    private BigDecimal totalHours;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private WorkedHoursStatus status;

    @Column(name = "observations", length = 500)
    private String observations;

    @Column(name = "validated_at")
    private LocalDateTime validatedAt;

    @Column(name = "validated_by_profile_id")
    private Long validatedByProfileId;
}
