package com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegisterMachineryResource(
        @NotNull Long categoryId,
        @NotBlank String name,
        String description,
        @NotBlank String brand,
        @NotBlank String model,
        @NotNull Integer manufactureYear,
        @NotNull @Valid MoneyResource hourlyRate,
        @NotNull @Valid MachineryLocationResource location) {
}
