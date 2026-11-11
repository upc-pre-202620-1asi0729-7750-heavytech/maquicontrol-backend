package com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record ScheduleMaintenanceResource(@NotNull Long machineryId, @NotBlank String type, @NotBlank String description,
                                          @NotNull LocalDateTime scheduledDate, String technicianName) {
}
