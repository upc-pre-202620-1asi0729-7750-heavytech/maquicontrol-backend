package com.andesheavytech.maquicontrol.platform.subscription.domain.model.aggregates;

import com.andesheavytech.maquicontrol.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.entities.Payment;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.events.SubscriptionActivatedEvent;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.events.SubscriptionCancelledEvent;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.events.SubscriptionPaymentApprovedEvent;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.events.SubscriptionRequestedEvent;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.SubscriptionStatus;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Subscription aggregate root: contracts and maintains an active subscription plan for a fleet owner.
 */
@Getter
public class Subscription extends AbstractDomainAggregateRoot<Subscription> {

    private final Long id;
    private final Long ownerProfileId;
    private final Long planId;
    private SubscriptionStatus status;
    private LocalDateTime startedAt;
    private LocalDateTime currentPeriodEnd;
    private String cancellationReason;
    private final List<Payment> payments;

    private Subscription(Long id, Long ownerProfileId, Long planId, SubscriptionStatus status,
                         LocalDateTime startedAt, LocalDateTime currentPeriodEnd,
                         String cancellationReason, List<Payment> payments) {
        if (ownerProfileId == null || ownerProfileId <= 0) {
            throw new IllegalArgumentException("ownerProfileId is required");
        }
        if (planId == null || planId <= 0) {
            throw new IllegalArgumentException("planId is required");
        }
        this.id = id;
        this.ownerProfileId = ownerProfileId;
        this.planId = planId;
        this.status = Objects.requireNonNull(status, "status is required");
        this.startedAt = startedAt;
        this.currentPeriodEnd = currentPeriodEnd;
        this.cancellationReason = cancellationReason;
        this.payments = new ArrayList<>(payments == null ? List.of() : payments);
    }

    /**
     * Subscribes to a plan in PENDING_PAYMENT status.
     */
    public static Subscription subscribe(Long ownerProfileId, Long planId) {
        return new Subscription(null, ownerProfileId, planId, SubscriptionStatus.PENDING_PAYMENT,
                null, null, null, List.of());
    }

    public static Subscription reconstitute(Long id, Long ownerProfileId, Long planId,
                                            SubscriptionStatus status, LocalDateTime startedAt,
                                            LocalDateTime currentPeriodEnd, String cancellationReason,
                                            List<Payment> payments) {
        return new Subscription(id, ownerProfileId, planId, status, startedAt, currentPeriodEnd,
                cancellationReason, payments);
    }

    /**
     * Registers the {@link SubscriptionRequestedEvent} after the ID has been assigned by persistence.
     */
    public void onRequested() {
        registerDomainEvent(new SubscriptionRequestedEvent(id, ownerProfileId, planId));
    }

    /**
     * Registers a payment transaction.
     */
    public void confirmPayment(Payment payment) {
        Objects.requireNonNull(payment, "payment is required");
        if (status == SubscriptionStatus.CANCELLED) {
            throw new IllegalStateException("cannot register a payment on a cancelled subscription");
        }
        this.payments.add(payment);
        if (payment.isApproved()) {
            registerDomainEvent(new SubscriptionPaymentApprovedEvent(id, payment.getId(), payment.getProviderPaymentId()));
        }
    }

    /**
     * Policy: activates the subscription after payment approval.
     */
    public void activate() {
        if (status != SubscriptionStatus.PENDING_PAYMENT) {
            throw new IllegalStateException("cannot activate a subscription that is %s".formatted(status));
        }
        this.status = SubscriptionStatus.ACTIVE;
        this.startedAt = LocalDateTime.now();
        this.currentPeriodEnd = startedAt.plusMonths(1);
        registerDomainEvent(new SubscriptionActivatedEvent(id, ownerProfileId, planId));
    }

    /**
     * Fleet owner cancels an active subscription.
     */
    public void cancel(String reason) {
        if (status != SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException("cannot cancel a subscription that is %s".formatted(status));
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason is required");
        }
        this.status = SubscriptionStatus.CANCELLED;
        this.cancellationReason = reason.trim();
        registerDomainEvent(new SubscriptionCancelledEvent(id, ownerProfileId, this.cancellationReason));
    }

    public boolean isActive() {
        return status == SubscriptionStatus.ACTIVE;
    }

    public boolean isPendingPayment() {
        return status == SubscriptionStatus.PENDING_PAYMENT;
    }

    public List<Payment> getPayments() {
        return Collections.unmodifiableList(payments);
    }
}
