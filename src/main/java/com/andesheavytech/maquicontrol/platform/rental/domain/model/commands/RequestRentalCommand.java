package com.andesheavytech.maquicontrol.platform.rental.domain.model.commands;

import java.time.LocalDate;

public record RequestRentalCommand(Long machineryId, Long contractorProfileId, LocalDate startDate, LocalDate endDate) {
}
