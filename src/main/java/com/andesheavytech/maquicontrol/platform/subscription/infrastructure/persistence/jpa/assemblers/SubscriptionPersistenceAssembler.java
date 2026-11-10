package com.andesheavytech.maquicontrol.platform.subscription.infrastructure.persistence.jpa.assemblers;

import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.aggregates.Subscription;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.entities.Payment;
import com.andesheavytech.maquicontrol.platform.subscription.infrastructure.persistence.jpa.entities.PaymentPersistenceEntity;
import com.andesheavytech.maquicontrol.platform.subscription.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Assembler between {@link Subscription} and {@link SubscriptionPersistenceEntity}.
 */
public final class SubscriptionPersistenceAssembler {

    private SubscriptionPersistenceAssembler() {
    }

    public static Subscription toDomainFromPersistence(SubscriptionPersistenceEntity entity) {
        if (entity == null) {
            return null;
        }
        var payments = entity.getPayments().stream()
                .map(p -> new Payment(
                        p.getId(),
                        entity.getId(),
                        p.getProvider(),
                        p.getProviderPaymentId(),
                        new Money(p.getAmount(), p.getCurrency()),
                        p.getStatus(),
                        p.getPaidAt()))
                .toList();

        return Subscription.reconstitute(
                entity.getId(),
                entity.getOwnerProfileId(),
                entity.getPlanId(),
                entity.getStatus(),
                entity.getStartedAt(),
                entity.getCurrentPeriodEnd(),
                entity.getCancellationReason(),
                payments
        );
    }

    public static SubscriptionPersistenceEntity copyToPersistence(Subscription subscription,
                                                                  SubscriptionPersistenceEntity entity) {
        entity.setOwnerProfileId(subscription.getOwnerProfileId());
        entity.setPlanId(subscription.getPlanId());
        entity.setStatus(subscription.getStatus());
        entity.setStartedAt(subscription.getStartedAt());
        entity.setCurrentPeriodEnd(subscription.getCurrentPeriodEnd());
        entity.setCancellationReason(subscription.getCancellationReason());

        Map<Long, PaymentPersistenceEntity> existing = new HashMap<>();
        entity.getPayments().forEach(p -> existing.put(p.getId(), p));

        for (var payment : subscription.getPayments()) {
            var target = payment.getId() == null ? null : existing.get(payment.getId());
            if (target == null) {
                target = new PaymentPersistenceEntity();
                entity.getPayments().add(target);
            }
            target.setProvider(payment.getProvider());
            target.setProviderPaymentId(payment.getProviderPaymentId());
            target.setAmount(payment.getAmount().amount());
            target.setCurrency(payment.getAmount().currency().name());
            target.setStatus(payment.getStatus());
            target.setPaidAt(payment.getPaidAt());
        }
        return entity;
    }
}
