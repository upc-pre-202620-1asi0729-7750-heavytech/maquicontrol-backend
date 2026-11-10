package com.andesheavytech.maquicontrol.platform.operations.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record ServiceOperationResource(
        Long id,
        Long rentalId,
        Long machineryId,
        Long operatorProfileId,
        String status,
        LocalDateTime startedAt,
        LocalDateTime completedAt,
        BigDecimal totalHours,
        List<WorkedHoursResource> workedHours
) {
}
