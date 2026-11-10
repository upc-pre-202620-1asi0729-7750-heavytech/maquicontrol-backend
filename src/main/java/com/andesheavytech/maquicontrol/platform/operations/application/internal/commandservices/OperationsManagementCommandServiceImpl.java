package com.andesheavytech.maquicontrol.platform.operations.application.internal.commandservices;

import com.andesheavytech.maquicontrol.platform.operations.application.commandservices.OperationsManagementCommandService;
import com.andesheavytech.maquicontrol.platform.operations.application.internal.outboundservices.acl.ExternalFleetService;
import com.andesheavytech.maquicontrol.platform.operations.application.internal.outboundservices.acl.ExternalRentalService;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.aggregates.ServiceOperation;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.commands.CompleteOperationCommand;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.commands.RecordWorkedHoursCommand;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.commands.RejectWorkedHoursCommand;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.commands.StartOperationCommand;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.commands.ValidateWorkedHoursCommand;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.entities.WorkedHours;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.Notes;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.WorkShift;
import com.andesheavytech.maquicontrol.platform.operations.domain.repositories.ServiceOperationRepository;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
 * Implementation of {@link OperationsManagementCommandService}.
 */
@Service
public class OperationsManagementCommandServiceImpl implements OperationsManagementCommandService {

    private final ServiceOperationRepository serviceOperationRepository;
    private final ExternalFleetService externalFleetService;
    private final ExternalRentalService externalRentalService;

    public OperationsManagementCommandServiceImpl(ServiceOperationRepository serviceOperationRepository,
                                                   @org.springframework.beans.factory.annotation.Qualifier("operationsExternalFleetService") ExternalFleetService externalFleetService,
                                                   @org.springframework.beans.factory.annotation.Qualifier("operationsExternalRentalService") ExternalRentalService externalRentalService) {
        this.serviceOperationRepository = serviceOperationRepository;
        this.externalFleetService = externalFleetService;
        this.externalRentalService = externalRentalService;
    }

    @Override
    @Transactional
    public Result<ServiceOperation, ApplicationError> handle(StartOperationCommand command) {
        if (!externalRentalService.existsRental(command.rentalId())) {
            return Result.failure(ApplicationError.notFound("Rental", String.valueOf(command.rentalId())));
        }
        var rentalStatus = externalRentalService.fetchRentalStatus(command.rentalId());
        if (!"CONFIRMED".equalsIgnoreCase(rentalStatus) && !"IN_PROGRESS".equalsIgnoreCase(rentalStatus)) {
            return Result.failure(ApplicationError.businessRuleViolation("rental status",
                    "only a confirmed rental can start a service operation (current: %s)".formatted(rentalStatus)));
        }
        if (!externalFleetService.existsMachinery(command.machineryId())) {
            return Result.failure(ApplicationError.notFound("Machinery", String.valueOf(command.machineryId())));
        }
        if (!command.machineryId().equals(externalRentalService.fetchRentalMachineryId(command.rentalId()))) {
            return Result.failure(ApplicationError.businessRuleViolation("machinery",
                    "the machinery does not belong to rental " + command.rentalId()));
        }
        if (!isRentalParticipant(command.rentalId(), command.machineryId(), command.requesterProfileId())) {
            return Result.failure(ApplicationError.forbidden("operation",
                    "only the contractor of the rental or the fleet owner can start the operation"));
        }
        if (serviceOperationRepository.existsByRentalId(command.rentalId())) {
            return Result.failure(ApplicationError.conflict("ServiceOperation",
                    "a service operation already exists for rental " + command.rentalId()));
        }
        try {
            var operation = ServiceOperation.start(command.rentalId(), command.machineryId(), command.operatorProfileId());
            return Result.success(serviceOperationRepository.save(operation));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Result.failure(ApplicationError.validationError("service operation", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<ServiceOperation, ApplicationError> handle(RecordWorkedHoursCommand command) {
        var found = serviceOperationRepository.findById(command.operationId());
        if (found.isEmpty()) {
            return Result.failure(ApplicationError.notFound("ServiceOperation", String.valueOf(command.operationId())));
        }
        var operation = found.get();
        var recorder = command.recorderProfileId();
        boolean isOperator = recorder != null && recorder.equals(operation.getOperatorProfileId());
        if (!isOperator && !isRentalParticipant(operation.getRentalId(), operation.getMachineryId(), recorder)) {
            return Result.failure(ApplicationError.forbidden("worked hours",
                    "only the operator, the contractor of the rental or the fleet owner can record worked hours"));
        }
        try {
            var shift = new WorkShift(command.workDate(), command.startTime(), command.endTime());
            var notes = new Notes(command.observations());
            var record = WorkedHours.record(shift, notes);
            operation.recordWorkedHours(record);
            return Result.success(serviceOperationRepository.save(operation));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Result.failure(ApplicationError.validationError("worked hours", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("service operation status", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<ServiceOperation, ApplicationError> handle(ValidateWorkedHoursCommand command) {
        var found = serviceOperationRepository.findById(command.operationId());
        if (found.isEmpty()) {
            return Result.failure(ApplicationError.notFound("ServiceOperation", String.valueOf(command.operationId())));
        }
        var operation = found.get();
        var ownerCheck = checkOwnerPermission(operation.getMachineryId(), command.validatorProfileId());
        if (ownerCheck != null) {
            return Result.failure(ownerCheck);
        }
        try {
            operation.validateWorkedHours(command.workedHoursId(), command.validatorProfileId());
            return Result.success(serviceOperationRepository.save(operation));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("worked hours", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("worked hours status", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<ServiceOperation, ApplicationError> handle(RejectWorkedHoursCommand command) {
        var found = serviceOperationRepository.findById(command.operationId());
        if (found.isEmpty()) {
            return Result.failure(ApplicationError.notFound("ServiceOperation", String.valueOf(command.operationId())));
        }
        var operation = found.get();
        var ownerCheck = checkOwnerPermission(operation.getMachineryId(), command.validatorProfileId());
        if (ownerCheck != null) {
            return Result.failure(ownerCheck);
        }
        try {
            operation.rejectWorkedHours(command.workedHoursId(), command.validatorProfileId());
            return Result.success(serviceOperationRepository.save(operation));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("worked hours", e.getMessage()));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("worked hours status", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<ServiceOperation, ApplicationError> handle(CompleteOperationCommand command) {
        var found = serviceOperationRepository.findById(command.operationId());
        if (found.isEmpty()) {
            return Result.failure(ApplicationError.notFound("ServiceOperation", String.valueOf(command.operationId())));
        }
        var operation = found.get();
        var ownerCheck = checkOwnerPermission(operation.getMachineryId(), command.validatorProfileId());
        if (ownerCheck != null) {
            return Result.failure(ownerCheck);
        }
        try {
            operation.complete();
            return Result.success(serviceOperationRepository.save(operation));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("service operation completion", e.getMessage()));
        }
    }

    private boolean isRentalParticipant(Long rentalId, Long machineryId, Long profileId) {
        if (profileId == null || profileId <= 0) {
            return false;
        }
        if (profileId.equals(externalRentalService.fetchRentalContractorProfileId(rentalId))) {
            return true;
        }
        return externalFleetService.fetchOwnerProfileId(machineryId).map(profileId::equals).orElse(false);
    }

    private ApplicationError checkOwnerPermission(Long machineryId, Long validatorProfileId) {
        if (validatorProfileId == null || validatorProfileId <= 0) {
            return ApplicationError.businessRuleViolation("profile required", "create your profile first");
        }
        var ownerIdOpt = externalFleetService.fetchOwnerProfileId(machineryId);
        if (ownerIdOpt.isPresent() && !ownerIdOpt.get().equals(validatorProfileId)) {
            return ApplicationError.forbidden("operation", "only the fleet owner can validate or complete operations");
        }
        return null;
    }
}
