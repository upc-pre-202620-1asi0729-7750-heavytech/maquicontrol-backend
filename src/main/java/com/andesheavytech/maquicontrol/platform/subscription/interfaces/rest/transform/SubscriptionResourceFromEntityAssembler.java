package com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.subscription.domain.model.aggregates.Subscription;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.entities.Payment;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.entities.SubscriptionPlan;
import com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest.resources.PaymentResource;
import com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest.resources.PlanResource;
import com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest.resources.SubscriptionResource;

import java.util.List;

public final class SubscriptionResourceFromEntityAssembler {

    private SubscriptionResourceFromEntityAssembler() {
    }

    public static PlanResource toResourceFromEntity(SubscriptionPlan plan) {
        if (plan == null) {
            return null;
        }
        return new PlanResource(
                plan.getId(),
                plan.getCode().name(),
                plan.getName(),
                plan.getMachineryLimit(),
                plan.getPrice().amount(),
                plan.getPrice().currency().name(),
                plan.getBillingCycle().name()
        );
    }

    public static SubscriptionResource toResourceFromEntity(Subscription subscription) {
        if (subscription == null) {
            return null;
        }
        List<PaymentResource> payments = subscription.getPayments().stream()
                .map(SubscriptionResourceFromEntityAssembler::toResourceFromEntity)
                .toList();

        return new SubscriptionResource(
                subscription.getId(),
                subscription.getOwnerProfileId(),
                subscription.getPlanId(),
                subscription.getStatus().name(),
                subscription.getStartedAt(),
                subscription.getCurrentPeriodEnd(),
                subscription.getCancellationReason(),
                payments
        );
    }

    public static PaymentResource toResourceFromEntity(Payment payment) {
        if (payment == null) {
            return null;
        }
        return new PaymentResource(
                payment.getId(),
                payment.getProvider().name(),
                payment.getProviderPaymentId(),
                payment.getAmount().amount(),
                payment.getAmount().currency().name(),
                payment.getStatus().name(),
                payment.getPaidAt()
        );
    }
}
