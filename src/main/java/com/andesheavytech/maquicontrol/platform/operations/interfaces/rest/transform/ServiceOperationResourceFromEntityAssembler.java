package com.andesheavytech.maquicontrol.platform.operations.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.operations.domain.model.aggregates.ServiceOperation;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.entities.WorkedHours;
import com.andesheavytech.maquicontrol.platform.operations.interfaces.rest.resources.ServiceOperationResource;
import com.andesheavytech.maquicontrol.platform.operations.interfaces.rest.resources.WorkedHoursResource;

import java.util.List;

public final class ServiceOperationResourceFromEntityAssembler {

    private ServiceOperationResourceFromEntityAssembler() {
    }

    public static ServiceOperationResource toResourceFromEntity(ServiceOperation operation) {
        if (operation == null) {
            return null;
        }
        List<WorkedHoursResource> workedHours = operation.getWorkedHours().stream()
                .map(ServiceOperationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return new ServiceOperationResource(
                operation.getId(),
                operation.getRentalId(),
                operation.getMachineryId(),
                operation.getOperatorProfileId(),
                operation.getStatus().name(),
                operation.getStartedAt(),
                operation.getCompletedAt(),
                operation.calculateTotalHours().value(),
                workedHours
        );
    }

    public static WorkedHoursResource toResourceFromEntity(WorkedHours record) {
        if (record == null) {
            return null;
        }
        return new WorkedHoursResource(
                record.getId(),
                record.getShift().workDate(),
                record.getShift().startTime(),
                record.getShift().endTime(),
                record.getTotalHours().value(),
                record.getStatus().name(),
                record.getObservations() == null ? null : record.getObservations().value(),
                record.getValidatedAt(),
                record.getValidatedByProfileId()
        );
    }
}
