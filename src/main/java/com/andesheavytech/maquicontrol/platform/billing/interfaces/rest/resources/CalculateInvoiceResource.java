package com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.resources;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Request body for calculating an invoice amount.
 */
public record CalculateInvoiceResource(
        @NotNull(message = "rentalId is required")
        Long rentalId,

        @NotNull(message = "amount is required")
        @DecimalMin(value = "0.00", message = "amount must be non-negative")
        BigDecimal amount,

        String currency
) {
}
