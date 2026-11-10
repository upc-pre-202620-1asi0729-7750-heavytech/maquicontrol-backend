package com.andesheavytech.maquicontrol.platform.subscription.domain.model.commands;

import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.PaymentStatus;

import java.math.BigDecimal;

public record ConfirmCheckoutCommand(Long subscriptionId, Long payerProfileId, String providerPaymentId, BigDecimal amount, String currency, PaymentStatus status) {
}
