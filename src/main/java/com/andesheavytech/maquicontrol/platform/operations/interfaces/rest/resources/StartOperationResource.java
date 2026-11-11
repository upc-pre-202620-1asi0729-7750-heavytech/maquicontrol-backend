package com.andesheavytech.maquicontrol.platform.operations.interfaces.rest.resources;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record StartOperationResource(
        @NotNull(message = "rentalId is required")
        @Positive(message = "rentalId must be positive")
        Long rentalId,

        @NotNull(message = "machineryId is required")
        @Positive(message = "machineryId must be positive")
        Long machineryId,

        @Positive(message = "operatorProfileId must be positive")
        Long operatorProfileId
) {
}
