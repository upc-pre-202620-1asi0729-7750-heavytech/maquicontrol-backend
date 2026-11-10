package com.andesheavytech.maquicontrol.platform.operations.infrastructure.persistence.jpa.assemblers;

import com.andesheavytech.maquicontrol.platform.operations.domain.model.aggregates.ServiceOperation;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.entities.WorkedHours;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.HoursWorked;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.Notes;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.WorkShift;
import com.andesheavytech.maquicontrol.platform.operations.infrastructure.persistence.jpa.entities.ServiceOperationPersistenceEntity;
import com.andesheavytech.maquicontrol.platform.operations.infrastructure.persistence.jpa.entities.WorkedHoursPersistenceEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Assembler between {@link ServiceOperation} and {@link ServiceOperationPersistenceEntity}.
 */
public final class ServiceOperationPersistenceAssembler {

    private ServiceOperationPersistenceAssembler() {
    }

    public static ServiceOperation toDomainFromPersistence(ServiceOperationPersistenceEntity entity) {
        var records = entity.getWorkedHours().stream()
                .map(r -> WorkedHours.reconstitute(
                        r.getId(),
                        new WorkShift(r.getWorkDate(), r.getStartTime(), r.getEndTime()),
                        new HoursWorked(r.getTotalHours()),
                        r.getStatus(),
                        new Notes(r.getObservations()),
                        r.getValidatedAt(),
                        r.getValidatedByProfileId()))
                .toList();

        return ServiceOperation.reconstitute(
                entity.getId(),
                entity.getRentalId(),
                entity.getMachineryId(),
                entity.getOperatorProfileId(),
                entity.getStatus(),
                entity.getStartedAt(),
                entity.getCompletedAt(),
                records);
    }

    public static ServiceOperationPersistenceEntity copyToPersistence(ServiceOperation operation,
                                                                      ServiceOperationPersistenceEntity entity) {
        entity.setRentalId(operation.getRentalId());
        entity.setMachineryId(operation.getMachineryId());
        entity.setOperatorProfileId(operation.getOperatorProfileId());
        entity.setStatus(operation.getStatus());
        entity.setStartedAt(operation.getStartedAt());
        entity.setCompletedAt(operation.getCompletedAt());

        Map<Long, WorkedHoursPersistenceEntity> existing = new HashMap<>();
        entity.getWorkedHours().forEach(r -> existing.put(r.getId(), r));

        for (var record : operation.getWorkedHours()) {
            var target = record.getId() == null ? null : existing.get(record.getId());
            if (target == null) {
                target = new WorkedHoursPersistenceEntity();
                entity.getWorkedHours().add(target);
            }
            target.setWorkDate(record.getShift().workDate());
            target.setStartTime(record.getShift().startTime());
            target.setEndTime(record.getShift().endTime());
            target.setTotalHours(record.getTotalHours().value());
            target.setStatus(record.getStatus());
            target.setObservations(record.getObservations() == null ? null : record.getObservations().value());
            target.setValidatedAt(record.getValidatedAt());
            target.setValidatedByProfileId(record.getValidatedByProfileId());
        }
        return entity;
    }
}
