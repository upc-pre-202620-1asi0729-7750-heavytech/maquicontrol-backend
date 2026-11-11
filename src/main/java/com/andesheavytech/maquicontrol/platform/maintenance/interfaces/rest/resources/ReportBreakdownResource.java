package com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ReportBreakdownResource(@NotNull Long machineryId, @NotBlank String description, @NotBlank String severity) {
}
