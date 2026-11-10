package com.andesheavytech.maquicontrol.platform.operations.domain.model.commands;

public record RejectWorkedHoursCommand(Long operationId, Long workedHoursId, Long validatorProfileId) {
}
