package com.andesheavytech.maquicontrol.platform.subscription.infrastructure.persistence.jpa.assemblers;

import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.entities.SubscriptionPlan;
import com.andesheavytech.maquicontrol.platform.subscription.infrastructure.persistence.jpa.entities.SubscriptionPlanPersistenceEntity;

/**
 * Assembler between {@link SubscriptionPlan} and {@link SubscriptionPlanPersistenceEntity}.
 */
public final class SubscriptionPlanPersistenceAssembler {

    private SubscriptionPlanPersistenceAssembler() {
    }

    public static SubscriptionPlan toDomainFromPersistence(SubscriptionPlanPersistenceEntity entity) {
        if (entity == null) {
            return null;
        }
        var price = new Money(entity.getPriceAmount(), entity.getPriceCurrency());
        return new SubscriptionPlan(
                entity.getId(),
                entity.getCode(),
                entity.getName(),
                entity.getMachineryLimit(),
                price,
                entity.getBillingCycle()
        );
    }

    public static SubscriptionPlanPersistenceEntity copyToPersistence(SubscriptionPlan plan,
                                                                      SubscriptionPlanPersistenceEntity entity) {
        entity.setCode(plan.getCode());
        entity.setName(plan.getName());
        entity.setMachineryLimit(plan.getMachineryLimit());
        entity.setPriceAmount(plan.getPrice().amount());
        entity.setPriceCurrency(plan.getPrice().currency().name());
        entity.setBillingCycle(plan.getBillingCycle());
        return entity;
    }
}
