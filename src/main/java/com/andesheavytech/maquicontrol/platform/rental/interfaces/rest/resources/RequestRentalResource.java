package com.andesheavytech.maquicontrol.platform.rental.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record RequestRentalResource(@NotNull Long machineryId, @NotNull LocalDate startDate, @NotNull LocalDate endDate) {
}
