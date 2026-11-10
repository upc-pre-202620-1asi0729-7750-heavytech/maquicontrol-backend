package com.andesheavytech.maquicontrol.platform.operations.domain.model.commands;

public record StartOperationCommand(Long rentalId, Long machineryId, Long operatorProfileId, Long requesterProfileId, boolean requesterIsOperator) {
}
