package com.andesheavytech.maquicontrol.platform.subscription.domain.model.entities;

import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.PaymentProviderName;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.PaymentStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entity representing a payment transaction for a subscription.
 */
@Getter
public class Payment {

    private final Long id;
    private final Long subscriptionId;
    private final PaymentProviderName provider;
    private final String providerPaymentId;
    private final Money amount;
    private PaymentStatus status;
    private LocalDateTime paidAt;

    public Payment(Long id, Long subscriptionId, PaymentProviderName provider, String providerPaymentId,
                   Money amount, PaymentStatus status, LocalDateTime paidAt) {
        this.id = id;
        this.subscriptionId = subscriptionId;
        this.provider = Objects.requireNonNull(provider, "provider is required");
        this.providerPaymentId = providerPaymentId;
        this.amount = Objects.requireNonNull(amount, "amount is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.paidAt = paidAt;
    }

    public static Payment of(Long subscriptionId, PaymentProviderName provider, String providerPaymentId,
                             Money amount, PaymentStatus status) {
        LocalDateTime paidAt = status == PaymentStatus.APPROVED ? LocalDateTime.now() : null;
        return new Payment(null, subscriptionId, provider, providerPaymentId, amount, status, paidAt);
    }

    public void approve() {
        this.status = PaymentStatus.APPROVED;
        this.paidAt = LocalDateTime.now();
    }

    public void reject() {
        this.status = PaymentStatus.REJECTED;
    }

    public boolean isApproved() {
        return status == PaymentStatus.APPROVED;
    }
}
