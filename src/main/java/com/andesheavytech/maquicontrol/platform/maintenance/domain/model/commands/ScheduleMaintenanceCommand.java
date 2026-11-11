package com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands;

import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.MaintenanceType;

import java.time.LocalDateTime;

public record ScheduleMaintenanceCommand(Long machineryId, Long ownerProfileId, MaintenanceType type, String description,
                                         LocalDateTime scheduledDate, String technicianName) {
}
