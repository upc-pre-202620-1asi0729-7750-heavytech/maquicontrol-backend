package com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands;

import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.MaintenanceStatus;

public record UpdateMaintenanceStatusCommand(Long maintenanceId, Long ownerProfileId, MaintenanceStatus status) {
}
