package com.andesheavytech.maquicontrol.platform.subscription.application.internal.eventhandlers;

import com.andesheavytech.maquicontrol.platform.subscription.application.commandservices.SubscriptionManagementCommandService;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.commands.ActivateSubscriptionCommand;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.events.SubscriptionActivatedEvent;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.events.SubscriptionCancelledEvent;
import com.andesheavytech.maquicontrol.platform.subscription.domain.model.events.SubscriptionPaymentApprovedEvent;
import com.andesheavytech.maquicontrol.platform.subscription.interfaces.events.SubscriptionActivatedIntegrationEvent;
import com.andesheavytech.maquicontrol.platform.subscription.interfaces.events.SubscriptionCancelledIntegrationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Handles Subscription domain events from the outbox relay and policies.
 */
@Service
@Slf4j
public class SubscriptionDomainEventHandler {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final SubscriptionManagementCommandService subscriptionManagementCommandService;

    public SubscriptionDomainEventHandler(ApplicationEventPublisher applicationEventPublisher,
                                          SubscriptionManagementCommandService subscriptionManagementCommandService) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.subscriptionManagementCommandService = subscriptionManagementCommandService;
    }

    @EventListener
    public void on(SubscriptionPaymentApprovedEvent event) {
        log.info("Payment approved for subscription {}: activating subscription", event.subscriptionId());
        subscriptionManagementCommandService.handle(new ActivateSubscriptionCommand(event.subscriptionId()));
    }

    @EventListener
    public void on(SubscriptionActivatedEvent event) {
        log.info("Subscription {} activated for owner {}", event.subscriptionId(), event.ownerProfileId());
        applicationEventPublisher.publishEvent(new SubscriptionActivatedIntegrationEvent(
                event.subscriptionId(), event.ownerProfileId(), event.planId()));
    }

    @EventListener
    public void on(SubscriptionCancelledEvent event) {
        log.info("Subscription {} cancelled for owner {}: {}", event.subscriptionId(), event.ownerProfileId(), event.reason());
        applicationEventPublisher.publishEvent(new SubscriptionCancelledIntegrationEvent(
                event.subscriptionId(), event.ownerProfileId()));
    }
}
