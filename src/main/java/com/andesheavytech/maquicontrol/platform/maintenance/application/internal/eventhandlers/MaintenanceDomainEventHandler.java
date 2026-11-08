package com.andesheavytech.maquicontrol.platform.maintenance.application.internal.eventhandlers;

import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.events.MaintenanceCompletedEvent;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.events.MaintenanceStatusUpdatedEvent;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.MaintenanceStatus;
import com.andesheavytech.maquicontrol.platform.maintenance.interfaces.events.MaintenanceCompletedIntegrationEvent;
import com.andesheavytech.maquicontrol.platform.maintenance.interfaces.events.MaintenanceStartedIntegrationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Turns Maintenance domain events (delivered by the outbox relay) into integration events that
 * other bounded contexts listen to.
 */
@Service
public class MaintenanceDomainEventHandler {

    private final ApplicationEventPublisher applicationEventPublisher;

    public MaintenanceDomainEventHandler(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @EventListener
    public void on(MaintenanceStatusUpdatedEvent event) {
        if (MaintenanceStatus.IN_PROGRESS.name().equals(event.status())) {
            applicationEventPublisher.publishEvent(
                    new MaintenanceStartedIntegrationEvent(event.maintenanceId(), event.machineryId()));
        }
    }

    @EventListener
    public void on(MaintenanceCompletedEvent event) {
        applicationEventPublisher.publishEvent(
                new MaintenanceCompletedIntegrationEvent(event.maintenanceId(), event.machineryId()));
    }
}
