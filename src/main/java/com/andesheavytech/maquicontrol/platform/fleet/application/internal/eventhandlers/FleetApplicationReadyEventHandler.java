package com.andesheavytech.maquicontrol.platform.fleet.application.internal.eventhandlers;

import com.andesheavytech.maquicontrol.platform.fleet.application.commandservices.FleetManagementCommandService;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.SeedCategoriesCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Seeds the default machinery categories when the application starts.
 */
@Service
@Slf4j
public class FleetApplicationReadyEventHandler {

    private final FleetManagementCommandService fleetManagementCommandService;

    public FleetApplicationReadyEventHandler(FleetManagementCommandService fleetManagementCommandService) {
        this.fleetManagementCommandService = fleetManagementCommandService;
    }

    @EventListener
    public void on(ApplicationReadyEvent event) {
        fleetManagementCommandService.handle(new SeedCategoriesCommand());
        log.info("Machinery categories seeding verified");
    }
}
