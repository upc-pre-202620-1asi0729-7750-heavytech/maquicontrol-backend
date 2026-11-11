package com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands;

public record CancelMaintenanceCommand(Long maintenanceId, Long ownerProfileId) {
}
