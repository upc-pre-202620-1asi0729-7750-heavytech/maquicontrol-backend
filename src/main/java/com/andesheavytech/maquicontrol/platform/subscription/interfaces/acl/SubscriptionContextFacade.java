package com.andesheavytech.maquicontrol.platform.subscription.interfaces.acl;

/**
 * Facade that other bounded contexts use to read Subscription data.
 */
public interface SubscriptionContextFacade {

    boolean hasActiveSubscription(Long ownerProfileId);

    int fetchMachineryLimit(Long ownerProfileId);
}
