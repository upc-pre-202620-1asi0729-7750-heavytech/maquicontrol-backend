package com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MoneyResource(@NotNull @Positive BigDecimal amount, @NotBlank String currency) {
}
