package com.andesheavytech.maquicontrol.platform.subscription.domain.model.commands;

public record CancelSubscriptionCommand(Long subscriptionId, Long ownerProfileId, String reason) {
}
