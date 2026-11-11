package com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands;

public record StartMaintenanceCommand(Long maintenanceId, Long ownerProfileId) {
}
