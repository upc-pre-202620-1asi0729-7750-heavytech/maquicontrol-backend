package com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest.resources;

import java.math.BigDecimal;

public record PlanResource(
        Long id,
        String code,
        String name,
        Integer machineryLimit,
        BigDecimal priceAmount,
        String priceCurrency,
        String billingCycle
) {
}
