package com.andesheavytech.maquicontrol.platform.maintenance.application.internal.eventhandlers;

import com.andesheavytech.maquicontrol.platform.maintenance.application.commandservices.MaintenanceManagementCommandService;
import com.andesheavytech.maquicontrol.platform.maintenance.application.internal.outboundservices.acl.ExternalFleetService;
import com.andesheavytech.maquicontrol.platform.maintenance.application.internal.outboundservices.acl.ExternalOperationsService;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.aggregates.Maintenance;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.ScheduleMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.MaintenanceStatus;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.MaintenanceType;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.repositories.MaintenanceRepository;
import com.andesheavytech.maquicontrol.platform.operations.interfaces.events.WorkedHoursValidatedIntegrationEvent;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;

/**
 * Policy "when accumulated hours reach the preventive threshold, schedule a preventive
 * maintenance": reacts to the worked hours validated in Operations Management.
 */
@Service
@Slf4j
public class OperationsIntegrationEventHandler {

    private final MaintenanceManagementCommandService maintenanceManagementCommandService;
    private final MaintenanceRepository maintenanceRepository;
    private final ExternalFleetService externalFleetService;
    private final ExternalOperationsService externalOperationsService;
    private final BigDecimal preventiveThresholdHours;

    public OperationsIntegrationEventHandler(MaintenanceManagementCommandService maintenanceManagementCommandService,
                                             MaintenanceRepository maintenanceRepository,
                                             @Qualifier("maintenanceExternalFleetService") ExternalFleetService externalFleetService,
                                             @Qualifier("maintenanceExternalOperationsService") ExternalOperationsService externalOperationsService,
                                             @Value("${maintenance.preventive.threshold-hours:250}") BigDecimal preventiveThresholdHours) {
        this.maintenanceManagementCommandService = maintenanceManagementCommandService;
        this.maintenanceRepository = maintenanceRepository;
        this.externalFleetService = externalFleetService;
        this.externalOperationsService = externalOperationsService;
        this.preventiveThresholdHours = preventiveThresholdHours;
    }

    @EventListener
    public void on(WorkedHoursValidatedIntegrationEvent event) {
        var maintenances = maintenanceRepository.findByMachineryId(event.machineryId());
        boolean preventivePending = maintenances.stream()
                .anyMatch(maintenance -> maintenance.getType() == MaintenanceType.PREVENTIVE
                        && (maintenance.getStatus() == MaintenanceStatus.SCHEDULED
                        || maintenance.getStatus() == MaintenanceStatus.IN_PROGRESS));
        if (preventivePending) {
            return;
        }
        LocalDateTime lastPreventive = maintenances.stream()
                .filter(maintenance -> maintenance.getType() == MaintenanceType.PREVENTIVE
                        && maintenance.getStatus() == MaintenanceStatus.COMPLETED)
                .map(Maintenance::getCompletedAt)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
        var accumulatedHours = externalOperationsService.fetchValidatedHoursSince(event.machineryId(), lastPreventive);
        if (accumulatedHours.compareTo(preventiveThresholdHours) < 0) {
            return;
        }
        var ownerProfileId = externalFleetService.fetchOwnerProfileId(event.machineryId()).orElse(null);
        var command = new ScheduleMaintenanceCommand(event.machineryId(), ownerProfileId, MaintenanceType.PREVENTIVE,
                "Preventive maintenance after %s operating hours".formatted(accumulatedHours.stripTrailingZeros().toPlainString()),
                LocalDateTime.now().plusDays(1), null);
        var result = maintenanceManagementCommandService.handle(command);
        if (result instanceof Result.Failure<?, ?> failure) {
            log.warn("Could not schedule the preventive maintenance of machinery {}: {}", event.machineryId(), failure.error());
        } else {
            log.info("Preventive maintenance scheduled for machinery {} after {} hours", event.machineryId(), accumulatedHours);
        }
    }
}
