package com.andesheavytech.maquicontrol.platform.rental.interfaces.rest.resources;

import java.time.LocalDate;

public record MachineryAvailabilityResource(Long machineryId, LocalDate startDate, LocalDate endDate, boolean available) {
}
