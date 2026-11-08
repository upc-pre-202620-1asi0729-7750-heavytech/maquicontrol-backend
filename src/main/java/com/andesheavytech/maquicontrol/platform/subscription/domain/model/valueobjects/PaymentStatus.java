package com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects;

/**
 * Payment transaction status (aligned with Mercado Pago statuses).
 */
public enum PaymentStatus {
    PENDING,
    IN_PROCESS,
    APPROVED,
    REJECTED,
    CANCELLED
}
