package com.andesheavytech.maquicontrol.platform.fleet.application.internal.eventhandlers;

import com.andesheavytech.maquicontrol.platform.fleet.application.commandservices.FleetManagementCommandService;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.MarkMachineryAsRentedCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.ReleaseMachineryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.ReserveMachineryCommand;
import com.andesheavytech.maquicontrol.platform.rental.interfaces.events.RentalClosedIntegrationEvent;
import com.andesheavytech.maquicontrol.platform.rental.interfaces.events.RentalConfirmedIntegrationEvent;
import com.andesheavytech.maquicontrol.platform.rental.interfaces.events.RentalStartedIntegrationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Policy: the status of a machine follows its rentals.
 * Confirmed rental reserves it, a started rental marks it as rented, and it goes back to
 * available when no rental keeps it committed.
 */
@Service
@Slf4j
public class RentalIntegrationEventHandler {

    private final FleetManagementCommandService fleetManagementCommandService;

    public RentalIntegrationEventHandler(FleetManagementCommandService fleetManagementCommandService) {
        this.fleetManagementCommandService = fleetManagementCommandService;
    }

    @EventListener
    public void on(RentalConfirmedIntegrationEvent event) {
        log.info("Rental {} confirmed: reserving machinery {}", event.rentalId(), event.machineryId());
        fleetManagementCommandService.handle(new ReserveMachineryCommand(event.machineryId()));
    }

    @EventListener
    public void on(RentalStartedIntegrationEvent event) {
        log.info("Rental {} started: machinery {} is rented", event.rentalId(), event.machineryId());
        fleetManagementCommandService.handle(new MarkMachineryAsRentedCommand(event.machineryId()));
    }

    @EventListener
    public void on(RentalClosedIntegrationEvent event) {
        if (event.machineryStillCommitted()) {
            log.info("Rental {} closed: machinery {} stays committed to another rental", event.rentalId(), event.machineryId());
            return;
        }
        log.info("Rental {} closed: releasing machinery {}", event.rentalId(), event.machineryId());
        fleetManagementCommandService.handle(new ReleaseMachineryCommand(event.machineryId()));
    }
}
