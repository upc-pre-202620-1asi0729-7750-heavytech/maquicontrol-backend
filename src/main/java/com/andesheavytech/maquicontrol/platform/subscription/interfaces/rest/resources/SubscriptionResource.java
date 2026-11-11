package com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest.resources;

import java.time.LocalDateTime;
import java.util.List;

public record SubscriptionResource(
        Long id,
        Long ownerProfileId,
        Long planId,
        String status,
        LocalDateTime startedAt,
        LocalDateTime currentPeriodEnd,
        String cancellationReason,
        List<PaymentResource> payments
) {
}
