package com.andesheavytech.maquicontrol.platform.maintenance.application.internal.commandservices;

import com.andesheavytech.maquicontrol.platform.maintenance.application.commandservices.MaintenanceManagementCommandService;
import com.andesheavytech.maquicontrol.platform.maintenance.application.internal.outboundservices.acl.ExternalFleetService;
import com.andesheavytech.maquicontrol.platform.maintenance.application.internal.outboundservices.acl.ExternalRentalService;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.aggregates.Maintenance;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.CancelMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.CompleteMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.ReportBreakdownCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.ScheduleMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.StartMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.entities.BreakdownReport;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.repositories.MaintenanceRepository;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * Implementation of {@link MaintenanceManagementCommandService}.
 */
@Service
public class MaintenanceManagementCommandServiceImpl implements MaintenanceManagementCommandService {

    private final MaintenanceRepository maintenanceRepository;
    private final ExternalFleetService externalFleetService;
    private final ExternalRentalService externalRentalService;

    public MaintenanceManagementCommandServiceImpl(MaintenanceRepository maintenanceRepository,
                                                   ExternalFleetService externalFleetService,
                                                   ExternalRentalService externalRentalService) {
        this.maintenanceRepository = maintenanceRepository;
        this.externalFleetService = externalFleetService;
        this.externalRentalService = externalRentalService;
    }

    @Override
    @Transactional
    public Result<Maintenance, ApplicationError> handle(ScheduleMaintenanceCommand command) {
        var ownerCheck = requireMachineryOwner(command.machineryId(), command.ownerProfileId());
        if (ownerCheck != null) {
            return Result.failure(ownerCheck);
        }
        try {
            var maintenance = Maintenance.schedule(command.machineryId(), command.type(), command.description(),
                    command.scheduledDate(), command.technicianName(), LocalDateTime.now());
            return Result.success(maintenanceRepository.save(maintenance));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Result.failure(ApplicationError.validationError("maintenance", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Maintenance, ApplicationError> handle(StartMaintenanceCommand command) {
        var found = findMaintenanceOfOwner(command.maintenanceId(), command.ownerProfileId());
        if (found.isFailure()) {
            return found;
        }
        var maintenance = found.getOrElse(null);
        if (externalFleetService.isMachineryCommittedToRental(maintenance.getMachineryId())) {
            return Result.failure(ApplicationError.businessRuleViolation("machinery in use",
                    "the machinery is reserved or rented; finish or cancel the rental first"));
        }
        return applyTransition(maintenance, Maintenance::start);
    }

    @Override
    @Transactional
    public Result<Maintenance, ApplicationError> handle(CancelMaintenanceCommand command) {
        var found = findMaintenanceOfOwner(command.maintenanceId(), command.ownerProfileId());
        if (found.isFailure()) {
            return found;
        }
        return applyTransition(found.getOrElse(null), Maintenance::cancel);
    }

    @Override
    @Transactional
    public Result<Maintenance, ApplicationError> handle(CompleteMaintenanceCommand command) {
        var found = findMaintenanceOfOwner(command.maintenanceId(), command.ownerProfileId());
        if (found.isFailure()) {
            return found;
        }
        Money cost;
        try {
            cost = toCost(command);
        } catch (IllegalArgumentException | NullPointerException e) {
            return Result.failure(ApplicationError.validationError("cost", e.getMessage()));
        }
        return applyTransition(found.getOrElse(null), maintenance -> maintenance.complete(cost));
    }

    @Override
    @Transactional
    public Result<Maintenance, ApplicationError> handle(ReportBreakdownCommand command) {
        if (command.reporterProfileId() == null || command.reporterProfileId() <= 0) {
            return Result.failure(ApplicationError.businessRuleViolation("profile required",
                    "create your profile before reporting a breakdown"));
        }
        if (!externalFleetService.existsMachinery(command.machineryId())) {
            return Result.failure(ApplicationError.notFound("Machinery", String.valueOf(command.machineryId())));
        }
        var isOwner = externalFleetService.fetchOwnerProfileId(command.machineryId())
                .filter(ownerProfileId -> ownerProfileId.equals(command.reporterProfileId())).isPresent();
        if (!isOwner && !externalRentalService.isRentingMachinery(command.reporterProfileId(), command.machineryId())) {
            return Result.failure(ApplicationError.forbidden("machinery",
                    "only the owner or a contractor with a rental in progress can report a breakdown"));
        }
        try {
            var report = BreakdownReport.report(command.description(), command.severity());
            var pending = maintenanceRepository.findPendingCorrectiveByMachineryId(command.machineryId());
            if (pending.isPresent()) {
                var maintenance = pending.get();
                maintenance.reportBreakdown(report);
                return Result.success(maintenanceRepository.save(maintenance));
            }
            // Policy: when a breakdown is reported, schedule corrective maintenance.
            var corrective = Maintenance.scheduleCorrective(command.machineryId(), report, LocalDateTime.now());
            return Result.success(maintenanceRepository.save(corrective));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Result.failure(ApplicationError.validationError("breakdown report", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("maintenance status", e.getMessage()));
        }
    }

    private ApplicationError requireMachineryOwner(Long machineryId, Long ownerProfileId) {
        if (ownerProfileId == null || ownerProfileId <= 0) {
            return ApplicationError.businessRuleViolation("profile required", "create your profile first");
        }
        if (!externalFleetService.existsMachinery(machineryId)) {
            return ApplicationError.notFound("Machinery", String.valueOf(machineryId));
        }
        var isOwner = externalFleetService.fetchOwnerProfileId(machineryId)
                .filter(id -> id.equals(ownerProfileId)).isPresent();
        return isOwner ? null : ApplicationError.forbidden("machinery", "only the owner can manage its maintenance");
    }

    private Result<Maintenance, ApplicationError> findMaintenanceOfOwner(Long maintenanceId, Long ownerProfileId) {
        var found = maintenanceRepository.findById(maintenanceId);
        if (found.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Maintenance", String.valueOf(maintenanceId)));
        }
        var ownerCheck = requireMachineryOwner(found.get().getMachineryId(), ownerProfileId);
        return ownerCheck == null ? Result.success(found.get()) : Result.failure(ownerCheck);
    }

    private Result<Maintenance, ApplicationError> applyTransition(Maintenance maintenance, Consumer<Maintenance> transition) {
        try {
            transition.accept(maintenance);
            return Result.success(maintenanceRepository.save(maintenance));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("maintenance status", e.getMessage()));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Result.failure(ApplicationError.validationError("maintenance", e.getMessage()));
        }
    }

    private static Money toCost(CompleteMaintenanceCommand command) {
        if (command.costAmount() == null && (command.costCurrency() == null || command.costCurrency().isBlank())) {
            return null;
        }
        return new Money(command.costAmount(), command.costCurrency());
    }
}
