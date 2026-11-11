package com.andesheavytech.maquicontrol.platform.operations.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record UpdateWorkedHoursStatusResource(
        @NotBlank(message = "status is required")
        @Schema(example = "VALIDATED", allowableValues = {"VALIDATED", "REJECTED"})
        String status
) {
}
