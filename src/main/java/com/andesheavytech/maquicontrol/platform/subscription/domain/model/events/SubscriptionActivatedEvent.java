package com.andesheavytech.maquicontrol.platform.subscription.domain.model.events;

/**
 * Domain event published when a subscription is activated.
 */
public record SubscriptionActivatedEvent(Long subscriptionId, Long ownerProfileId, Long planId) {
}
