package com.andesheavytech.maquicontrol.platform.operations.application.internal.eventhandlers;

import com.andesheavytech.maquicontrol.platform.operations.domain.model.events.OperationCompletedEvent;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.events.OperationStartedEvent;
import com.andesheavytech.maquicontrol.platform.operations.interfaces.events.OperationCompletedIntegrationEvent;
import com.andesheavytech.maquicontrol.platform.operations.interfaces.events.OperationStartedIntegrationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Turns Operations domain events delivered by the outbox relay into integration events.
 */
@Service
@Slf4j
public class OperationsDomainEventHandler {

    private final ApplicationEventPublisher applicationEventPublisher;

    public OperationsDomainEventHandler(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @EventListener
    public void on(OperationStartedEvent event) {
        log.info("Operation {} started for rental {} on machinery {}", event.operationId(), event.rentalId(), event.machineryId());
        applicationEventPublisher.publishEvent(new OperationStartedIntegrationEvent(event.operationId(), event.rentalId(), event.machineryId()));
    }

    @EventListener
    public void on(OperationCompletedEvent event) {
        log.info("Operation {} completed for rental {} on machinery {} with {} total hours",
                event.operationId(), event.rentalId(), event.machineryId(), event.totalHours());
        applicationEventPublisher.publishEvent(new OperationCompletedIntegrationEvent(
                event.operationId(), event.rentalId(), event.machineryId(), event.totalHours()));
    }
}
