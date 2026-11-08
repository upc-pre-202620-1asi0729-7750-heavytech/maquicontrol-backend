package com.andesheavytech.maquicontrol.platform.subscription.domain.model.entities;

import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.BillingCycle;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.PlanCode;
import lombok.Getter;

import java.util.Objects;

/**
 * Entity representing a subscription plan (e.g. Essential or Pro).
 */
@Getter
public class SubscriptionPlan {

    private final Long id;
    private final PlanCode code;
    private final String name;
    private final Integer machineryLimit;
    private final Money price;
    private final BillingCycle billingCycle;

    public SubscriptionPlan(Long id, PlanCode code, String name, Integer machineryLimit, Money price, BillingCycle billingCycle) {
        this.id = id;
        this.code = Objects.requireNonNull(code, "code is required");
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name is required");
        }
        this.name = name.trim();
        if (machineryLimit == null || machineryLimit <= 0) {
            throw new IllegalArgumentException("machineryLimit must be positive");
        }
        this.machineryLimit = machineryLimit;
        this.price = Objects.requireNonNull(price, "price is required");
        this.billingCycle = Objects.requireNonNull(billingCycle, "billingCycle is required");
    }

    public boolean allowsAdditionalMachinery(int currentCount) {
        return currentCount < machineryLimit;
    }
}
