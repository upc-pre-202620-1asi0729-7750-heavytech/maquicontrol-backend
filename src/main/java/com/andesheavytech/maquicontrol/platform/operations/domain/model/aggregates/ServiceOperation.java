package com.andesheavytech.maquicontrol.platform.operations.domain.model.aggregates;

import com.andesheavytech.maquicontrol.platform.operations.domain.model.entities.WorkedHours;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.events.OperationCompletedEvent;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.events.OperationStartedEvent;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.events.WorkedHoursRecordedEvent;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.events.WorkedHoursRejectedEvent;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.events.WorkedHoursValidatedEvent;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.HoursWorked;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.OperationStatus;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * ServiceOperation aggregate root: tracks machinery field work for a confirmed rental.
 */
@Getter
public class ServiceOperation extends AbstractDomainAggregateRoot<ServiceOperation> {

    private final Long id;
    private final Long rentalId;
    private final Long machineryId;
    private final Long operatorProfileId;
    private OperationStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private final List<WorkedHours> workedHours;

    private ServiceOperation(Long id, Long rentalId, Long machineryId, Long operatorProfileId,
                             OperationStatus status, LocalDateTime startedAt, LocalDateTime completedAt,
                             List<WorkedHours> workedHours) {
        if (rentalId == null || rentalId <= 0) {
            throw new IllegalArgumentException("rentalId is required");
        }
        if (machineryId == null || machineryId <= 0) {
            throw new IllegalArgumentException("machineryId is required");
        }
        if (operatorProfileId == null || operatorProfileId <= 0) {
            throw new IllegalArgumentException("operatorProfileId is required");
        }
        this.id = id;
        this.rentalId = rentalId;
        this.machineryId = machineryId;
        this.operatorProfileId = operatorProfileId;
        this.status = Objects.requireNonNull(status, "status is required");
        this.startedAt = startedAt;
        this.completedAt = completedAt;
        this.workedHours = new ArrayList<>(workedHours == null ? List.of() : workedHours);
    }

    /**
     * Starts a service operation for a confirmed rental.
     */
    public static ServiceOperation start(Long rentalId, Long machineryId, Long operatorProfileId) {
        return new ServiceOperation(null, rentalId, machineryId, operatorProfileId,
                OperationStatus.IN_PROGRESS, LocalDateTime.now(), null, List.of());
    }

    public static ServiceOperation reconstitute(Long id, Long rentalId, Long machineryId, Long operatorProfileId,
                                                OperationStatus status, LocalDateTime startedAt,
                                                LocalDateTime completedAt, List<WorkedHours> workedHours) {
        return new ServiceOperation(id, rentalId, machineryId, operatorProfileId, status, startedAt,
                completedAt, workedHours);
    }

    /**
     * Registers the {@link OperationStartedEvent} once the ID has been assigned by persistence.
     */
    public void onStarted() {
        registerDomainEvent(new OperationStartedEvent(id, rentalId, machineryId));
    }

    /**
     * Records a new shift of worked hours.
     */
    public void recordWorkedHours(WorkedHours record) {
        Objects.requireNonNull(record, "record is required");
        requireStatus(OperationStatus.IN_PROGRESS, "record worked hours");
        this.workedHours.add(record);
        registerDomainEvent(new WorkedHoursRecordedEvent(id, record.getId(), record.getTotalHours().value()));
    }

    /**
     * Validates a recorded shift.
     */
    public void validateWorkedHours(Long workedHoursId, Long validatorProfileId) {
        requireStatus(OperationStatus.IN_PROGRESS, "validate worked hours");
        var record = findWorkedHours(workedHoursId);
        record.validate(validatorProfileId);
        registerDomainEvent(new WorkedHoursValidatedEvent(id, workedHoursId, record.getTotalHours().value()));
    }

    /**
     * Rejects a recorded shift.
     */
    public void rejectWorkedHours(Long workedHoursId, Long validatorProfileId) {
        requireStatus(OperationStatus.IN_PROGRESS, "reject worked hours");
        var record = findWorkedHours(workedHoursId);
        record.reject(validatorProfileId);
        registerDomainEvent(new WorkedHoursRejectedEvent(id, workedHoursId));
    }

    /**
     * Completes the service operation. Business rule: no worked hours may remain PENDING.
     */
    public void complete() {
        requireStatus(OperationStatus.IN_PROGRESS, "complete operation");
        boolean hasPending = workedHours.stream().anyMatch(WorkedHours::isPending);
        if (hasPending) {
            throw new IllegalStateException("cannot complete operation while worked hours records remain pending validation");
        }
        this.status = OperationStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        registerDomainEvent(new OperationCompletedEvent(id, rentalId, machineryId, calculateTotalHours().value()));
    }

    /**
     * Sum of validated worked hours.
     */
    public HoursWorked calculateTotalHours() {
        return workedHours.stream()
                .filter(WorkedHours::isValidated)
                .map(WorkedHours::getTotalHours)
                .reduce(HoursWorked.ZERO, HoursWorked::add);
    }

    public boolean isInProgress() {
        return status == OperationStatus.IN_PROGRESS;
    }

    public List<WorkedHours> getWorkedHours() {
        return Collections.unmodifiableList(workedHours);
    }

    private WorkedHours findWorkedHours(Long workedHoursId) {
        return workedHours.stream()
                .filter(wh -> wh.getId() != null && wh.getId().equals(workedHoursId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("worked hours record %d not found".formatted(workedHoursId)));
    }

    private void requireStatus(OperationStatus expected, String action) {
        if (this.status != expected) {
            throw new IllegalStateException("cannot %s on an operation that is %s".formatted(action, status));
        }
    }
}
