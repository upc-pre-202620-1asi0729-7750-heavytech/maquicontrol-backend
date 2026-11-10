package com.andesheavytech.maquicontrol.platform.subscription.interfaces.events;

/**
 * Integration event emitted when a subscription is cancelled.
 */
public record SubscriptionCancelledIntegrationEvent(Long subscriptionId, Long ownerProfileId) {
}
