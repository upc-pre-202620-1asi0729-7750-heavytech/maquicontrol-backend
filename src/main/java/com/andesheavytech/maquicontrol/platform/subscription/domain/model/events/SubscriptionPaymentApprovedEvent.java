package com.andesheavytech.maquicontrol.platform.subscription.domain.model.events;

/**
 * Domain event published when a subscription payment is approved.
 */
public record SubscriptionPaymentApprovedEvent(Long subscriptionId, Long paymentId, String providerPaymentId) {
}
