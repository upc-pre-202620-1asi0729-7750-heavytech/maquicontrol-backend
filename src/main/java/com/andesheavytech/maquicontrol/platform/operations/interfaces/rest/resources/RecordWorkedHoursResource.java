package com.andesheavytech.maquicontrol.platform.operations.interfaces.rest.resources;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalTime;

public record RecordWorkedHoursResource(
        @NotNull(message = "workDate is required")
        @Schema(example = "2026-11-20")
        LocalDate workDate,

        @NotNull(message = "startTime is required")
        @Schema(type = "string", example = "08:00:00")
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime startTime,

        @NotNull(message = "endTime is required")
        @Schema(type = "string", example = "17:00:00")
        @JsonFormat(pattern = "HH:mm:ss")
        LocalTime endTime,

        @Size(max = 500, message = "observations cannot exceed 500 characters")
        @Schema(example = "Excavation shift completed")
        String observations
) {
}
