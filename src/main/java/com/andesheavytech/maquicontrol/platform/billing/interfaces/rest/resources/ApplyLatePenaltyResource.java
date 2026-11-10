package com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.resources;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Request body for applying a late penalty to a rental.
 */
public record ApplyLatePenaltyResource(
        @NotNull(message = "rentalId is required")
        Long rentalId,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.01", message = "penalty amount must be positive")
        BigDecimal amount,

        String currency,

        @NotBlank(message = "reason is required")
        String reason
) {
}
