package com.andesheavytech.maquicontrol.platform.operations.domain.model.entities;

import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.HoursWorked;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.Notes;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.WorkShift;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.WorkedHoursStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a shift record of worked hours within a service operation.
 */
@Getter
public class WorkedHours {

    private final Long id;
    private final WorkShift shift;
    private final HoursWorked totalHours;
    private WorkedHoursStatus status;
    private final Notes observations;
    private LocalDateTime validatedAt;
    private Long validatedByProfileId;

    private WorkedHours(Long id, WorkShift shift, HoursWorked totalHours, WorkedHoursStatus status,
                        Notes observations, LocalDateTime validatedAt, Long validatedByProfileId) {
        this.id = id;
        this.shift = Objects.requireNonNull(shift, "shift is required");
        this.totalHours = totalHours != null ? totalHours : shift.duration();
        this.status = Objects.requireNonNull(status, "status is required");
        this.observations = observations != null ? observations : new Notes(null);
        this.validatedAt = validatedAt;
        this.validatedByProfileId = validatedByProfileId;
    }

    /**
     * Records a new shift in PENDING status.
     */
    public static WorkedHours record(WorkShift shift, Notes observations) {
        return new WorkedHours(null, shift, shift.duration(), WorkedHoursStatus.PENDING, observations, null, null);
    }

    public static WorkedHours reconstitute(Long id, WorkShift shift, HoursWorked totalHours,
                                          WorkedHoursStatus status, Notes observations,
                                          LocalDateTime validatedAt, Long validatedByProfileId) {
        return new WorkedHours(id, shift, totalHours, status, observations, validatedAt, validatedByProfileId);
    }

    /**
     * Validates this record.
     */
    public void validate(Long validatorProfileId) {
        if (status != WorkedHoursStatus.PENDING) {
            throw new IllegalStateException("cannot validate a worked hours record that is %s".formatted(status));
        }
        if (validatorProfileId == null || validatorProfileId <= 0) {
            throw new IllegalArgumentException("validatorProfileId is required");
        }
        this.status = WorkedHoursStatus.VALIDATED;
        this.validatedAt = LocalDateTime.now();
        this.validatedByProfileId = validatorProfileId;
    }

    /**
     * Rejects this record.
     */
    public void reject(Long validatorProfileId) {
        if (status != WorkedHoursStatus.PENDING) {
            throw new IllegalStateException("cannot reject a worked hours record that is %s".formatted(status));
        }
        this.status = WorkedHoursStatus.REJECTED;
        this.validatedAt = LocalDateTime.now();
        this.validatedByProfileId = validatorProfileId;
    }

    public boolean isValidated() {
        return status == WorkedHoursStatus.VALIDATED;
    }

    public boolean isPending() {
        return status == WorkedHoursStatus.PENDING;
    }
}
