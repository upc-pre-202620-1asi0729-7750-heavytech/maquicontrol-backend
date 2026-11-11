package com.andesheavytech.maquicontrol.platform.operations.domain.model.commands;

public record ValidateWorkedHoursCommand(Long operationId, Long workedHoursId, Long validatorProfileId) {
}
