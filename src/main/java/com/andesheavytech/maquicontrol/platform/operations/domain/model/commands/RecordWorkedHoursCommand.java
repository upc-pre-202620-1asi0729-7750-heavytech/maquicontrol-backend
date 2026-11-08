package com.andesheavytech.maquicontrol.platform.operations.domain.model.commands;

import java.time.LocalDate;
import java.time.LocalTime;

public record RecordWorkedHoursCommand(Long operationId, LocalDate workDate, LocalTime startTime, LocalTime endTime, String observations) {
}
