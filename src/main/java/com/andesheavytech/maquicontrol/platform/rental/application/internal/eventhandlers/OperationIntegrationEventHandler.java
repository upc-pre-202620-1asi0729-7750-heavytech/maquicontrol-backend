package com.andesheavytech.maquicontrol.platform.rental.application.internal.eventhandlers;

import com.andesheavytech.maquicontrol.platform.operations.interfaces.events.OperationCompletedIntegrationEvent;
import com.andesheavytech.maquicontrol.platform.operations.interfaces.events.OperationStartedIntegrationEvent;
import com.andesheavytech.maquicontrol.platform.rental.application.commandservices.RentalManagementCommandService;
import com.andesheavytech.maquicontrol.platform.rental.application.queryservices.RentalManagementQueryService;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.CompleteRentalCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.StartRentalCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalByIdQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects.RentalStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Policy: When an operation starts, start the confirmed rental.
 * When an operation completes, complete the rental in progress.
 */
@Service
@Slf4j
public class OperationIntegrationEventHandler {

    private final RentalManagementCommandService rentalManagementCommandService;
    private final RentalManagementQueryService rentalManagementQueryService;

    public OperationIntegrationEventHandler(RentalManagementCommandService rentalManagementCommandService,
                                            RentalManagementQueryService rentalManagementQueryService) {
        this.rentalManagementCommandService = rentalManagementCommandService;
        this.rentalManagementQueryService = rentalManagementQueryService;
    }

    @EventListener
    public void on(OperationStartedIntegrationEvent event) {
        var rentalOpt = rentalManagementQueryService.handle(new GetRentalByIdQuery(event.rentalId()));
        if (rentalOpt.isPresent() && rentalOpt.get().getStatus() == RentalStatus.CONFIRMED) {
            log.info("Operation started: starting rental {}", event.rentalId());
            rentalManagementCommandService.handle(new StartRentalCommand(event.rentalId()));
        }
    }

    @EventListener
    public void on(OperationCompletedIntegrationEvent event) {
        var rentalOpt = rentalManagementQueryService.handle(new GetRentalByIdQuery(event.rentalId()));
        if (rentalOpt.isPresent() && rentalOpt.get().getStatus() == RentalStatus.IN_PROGRESS) {
            log.info("Operation completed: completing rental {}", event.rentalId());
            rentalManagementCommandService.handle(new CompleteRentalCommand(event.rentalId()));
        }
    }
}
