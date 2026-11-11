package com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

public record ConfirmCheckoutResource(
        @Schema(example = "MP-PAY-12345")
        String providerPaymentId,

        @Schema(example = "149.00")
        BigDecimal amount,

        @Schema(example = "PEN")
        String currency,

        @Schema(example = "APPROVED", allowableValues = {"APPROVED", "PENDING", "REJECTED"})
        String status
) {
}
