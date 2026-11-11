package com.andesheavytech.maquicontrol.platform.subscription.infrastructure.seeding;

import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.entities.SubscriptionPlan;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.BillingCycle;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.PlanCode;
import com.andesheavytech.maquicontrol.platform.subscription.domain.repositories.SubscriptionPlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

/**
 * Seeds default subscription plans (Essential and Pro) if empty.
 */
@Component
@Slf4j
public class SubscriptionPlanSeeder {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionPlanSeeder(SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void seed() {
        if (subscriptionPlanRepository.count() > 0) {
            return;
        }
        log.info("Seeding default subscription plans: ESSENTIAL and PRO");
        var essential = new SubscriptionPlan(
                null,
                PlanCode.ESSENTIAL,
                "Essential Plan",
                5,
                new Money(new BigDecimal("149.00"), "PEN"),
                BillingCycle.MONTHLY
        );
        var pro = new SubscriptionPlan(
                null,
                PlanCode.PRO,
                "Pro Plan",
                20,
                new Money(new BigDecimal("399.00"), "PEN"),
                BillingCycle.MONTHLY
        );
        subscriptionPlanRepository.save(essential);
        subscriptionPlanRepository.save(pro);
        log.info("Subscription plans seeded successfully");
    }
}
