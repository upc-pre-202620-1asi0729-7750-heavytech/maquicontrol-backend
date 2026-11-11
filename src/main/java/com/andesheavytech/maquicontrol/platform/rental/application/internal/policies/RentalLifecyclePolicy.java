package com.andesheavytech.maquicontrol.platform.rental.application.internal.policies;

import com.andesheavytech.maquicontrol.platform.rental.application.commandservices.RentalManagementCommandService;
import com.andesheavytech.maquicontrol.platform.rental.application.queryservices.RentalManagementQueryService;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.aggregates.Rental;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.CompleteRentalCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.StartRentalCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalsDueToCompleteQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalsDueToStartQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * Policies "Start Rental" and "Complete Rental": they have no endpoint. A confirmed rental starts
 * on its start date and a rental in progress completes the day after its end date.
 */
@Component
@Slf4j
public class RentalLifecyclePolicy {

    private final RentalManagementCommandService rentalManagementCommandService;
    private final RentalManagementQueryService rentalManagementQueryService;

    public RentalLifecyclePolicy(RentalManagementCommandService rentalManagementCommandService,
                                 RentalManagementQueryService rentalManagementQueryService) {
        this.rentalManagementCommandService = rentalManagementCommandService;
        this.rentalManagementQueryService = rentalManagementQueryService;
    }

    @Scheduled(initialDelayString = "${rental.lifecycle.initial-delay-ms:10000}",
            fixedDelayString = "${rental.lifecycle.fixed-delay-ms:60000}")
    public void applyLifecyclePolicies() {
        var today = LocalDate.now();
        rentalManagementQueryService.handle(new GetRentalsDueToStartQuery(today)).stream()
                .map(Rental::getId)
                .forEach(rentalId -> {
                    log.info("Starting rental {}", rentalId);
                    rentalManagementCommandService.handle(new StartRentalCommand(rentalId));
                });
        rentalManagementQueryService.handle(new GetRentalsDueToCompleteQuery(today)).stream()
                .map(Rental::getId)
                .forEach(rentalId -> {
                    log.info("Completing rental {}", rentalId);
                    rentalManagementCommandService.handle(new CompleteRentalCommand(rentalId));
                });
    }
}
