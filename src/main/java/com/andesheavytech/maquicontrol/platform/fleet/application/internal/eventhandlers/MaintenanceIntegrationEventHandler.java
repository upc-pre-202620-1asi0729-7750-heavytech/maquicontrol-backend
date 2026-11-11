package com.andesheavytech.maquicontrol.platform.fleet.application.internal.eventhandlers;

import com.andesheavytech.maquicontrol.platform.fleet.application.commandservices.FleetManagementCommandService;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.FinishMachineryMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.StartMachineryMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.interfaces.events.MaintenanceCompletedIntegrationEvent;
import com.andesheavytech.maquicontrol.platform.maintenance.interfaces.events.MaintenanceStartedIntegrationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Policy: a machine is IN_MAINTENANCE while a maintenance is in progress and goes back to
 * AVAILABLE when it is completed.
 */
@Service
@Slf4j
public class MaintenanceIntegrationEventHandler {

    private final FleetManagementCommandService fleetManagementCommandService;

    public MaintenanceIntegrationEventHandler(FleetManagementCommandService fleetManagementCommandService) {
        this.fleetManagementCommandService = fleetManagementCommandService;
    }

    @EventListener
    public void on(MaintenanceStartedIntegrationEvent event) {
        log.info("Maintenance {} started: machinery {} is in maintenance", event.maintenanceId(), event.machineryId());
        fleetManagementCommandService.handle(new StartMachineryMaintenanceCommand(event.machineryId()));
    }

    @EventListener
    public void on(MaintenanceCompletedIntegrationEvent event) {
        log.info("Maintenance {} completed: machinery {} is available again", event.maintenanceId(), event.machineryId());
        fleetManagementCommandService.handle(new FinishMachineryMaintenanceCommand(event.machineryId()));
    }
}
