package com.andesheavytech.maquicontrol.platform.operations.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record WorkedHoursResource(
        Long id,
        LocalDate workDate,
        LocalTime startTime,
        LocalTime endTime,
        BigDecimal totalHours,
        String status,
        String observations,
        LocalDateTime validatedAt,
        Long validatedByProfileId
) {
}
