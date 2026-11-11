package com.andesheavytech.maquicontrol.platform.operations.domain.model.commands;

public record CompleteOperationCommand(Long operationId, Long validatorProfileId) {
}
