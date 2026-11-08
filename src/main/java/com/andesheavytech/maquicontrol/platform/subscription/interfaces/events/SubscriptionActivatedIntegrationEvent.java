package com.andesheavytech.maquicontrol.platform.subscription.interfaces.events;

/**
 * Integration event emitted when a subscription is activated.
 */
public record SubscriptionActivatedIntegrationEvent(Long subscriptionId, Long ownerProfileId, Long planId) {
}
