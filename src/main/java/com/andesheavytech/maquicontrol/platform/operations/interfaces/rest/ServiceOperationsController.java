package com.andesheavytech.maquicontrol.platform.operations.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.operations.application.commandservices.OperationsManagementCommandService;
import com.andesheavytech.maquicontrol.platform.operations.application.queryservices.OperationsManagementQueryService;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.aggregates.ServiceOperation;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.commands.CompleteOperationCommand;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.commands.RecordWorkedHoursCommand;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.commands.RejectWorkedHoursCommand;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.commands.StartOperationCommand;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.commands.ValidateWorkedHoursCommand;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.queries.GetOperationsByMachineryIdQuery;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.queries.GetOperationsByOperatorProfileIdQuery;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.queries.ViewServiceOperationByRentalIdQuery;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.queries.ViewServiceOperationQuery;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.queries.ViewWorkedHoursQuery;
import com.andesheavytech.maquicontrol.platform.operations.interfaces.rest.resources.RecordWorkedHoursResource;
import com.andesheavytech.maquicontrol.platform.operations.interfaces.rest.resources.ServiceOperationResource;
import com.andesheavytech.maquicontrol.platform.operations.interfaces.rest.resources.StartOperationResource;
import com.andesheavytech.maquicontrol.platform.operations.interfaces.rest.resources.UpdateOperationStatusResource;
import com.andesheavytech.maquicontrol.platform.operations.interfaces.rest.resources.UpdateWorkedHoursStatusResource;
import com.andesheavytech.maquicontrol.platform.operations.interfaces.rest.resources.WorkedHoursResource;
import com.andesheavytech.maquicontrol.platform.operations.interfaces.rest.transform.ServiceOperationResourceFromEntityAssembler;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.acl.ProfilesContextFacade;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;
import com.andesheavytech.maquicontrol.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for service operations and worked hours management.
 */
@RestController
@RequestMapping(value = "/api/v1/service-operations", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Service Operations", description = "Service operations and worked hours tracking endpoints")
public class ServiceOperationsController {

    private final OperationsManagementCommandService operationsManagementCommandService;
    private final OperationsManagementQueryService operationsManagementQueryService;
    private final ProfilesContextFacade profilesContextFacade;

    public ServiceOperationsController(OperationsManagementCommandService operationsManagementCommandService,
                                       OperationsManagementQueryService operationsManagementQueryService,
                                       ProfilesContextFacade profilesContextFacade) {
        this.operationsManagementCommandService = operationsManagementCommandService;
        this.operationsManagementQueryService = operationsManagementQueryService;
        this.profilesContextFacade = profilesContextFacade;
    }

    @PostMapping
    @Operation(summary = "Start service operation", description = "Starts the field operation for a confirmed rental.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Operation started",
                    content = @Content(schema = @Schema(implementation = ServiceOperationResource.class))),
            @ApiResponse(responseCode = "400", description = "Validation error"),
            @ApiResponse(responseCode = "404", description = "Rental or machinery not found"),
            @ApiResponse(responseCode = "409", description = "Operation already exists for rental"),
            @ApiResponse(responseCode = "422", description = "Rental not in CONFIRMED status")
    })
    public ResponseEntity<?> startOperation(@Valid @RequestBody StartOperationResource resource,
                                            Authentication authentication) {
        var requesterProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        Long operatorProfileId = resource.operatorProfileId();
        if (operatorProfileId == null || operatorProfileId <= 0) {
            operatorProfileId = requesterProfileId;
        }
        var command = new StartOperationCommand(resource.rentalId(), resource.machineryId(), operatorProfileId, requesterProfileId);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                operationsManagementCommandService.handle(command),
                ServiceOperationResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }

    @PostMapping("/{operationId}/worked-hours")
    @Operation(summary = "Record worked hours", description = "Records a work shift in PENDING status for the operation.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Worked hours recorded",
                    content = @Content(schema = @Schema(implementation = ServiceOperationResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid shift dates or times"),
            @ApiResponse(responseCode = "404", description = "Operation not found"),
            @ApiResponse(responseCode = "422", description = "Operation not IN_PROGRESS")
    })
    public ResponseEntity<?> recordWorkedHours(@PathVariable Long operationId,
                                               @Valid @RequestBody RecordWorkedHoursResource resource,
                                               Authentication authentication) {
        var recorderProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        var command = new RecordWorkedHoursCommand(operationId, resource.workDate(), resource.startTime(),
                resource.endTime(), resource.observations(), recorderProfileId);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                operationsManagementCommandService.handle(command),
                ServiceOperationResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.CREATED);
    }

    @PatchMapping("/{operationId}/worked-hours/{workedHoursId}/status")
    @Operation(summary = "Validate or reject worked hours",
            description = "Fleet owner validates or rejects a recorded work shift.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Worked hours status updated",
                    content = @Content(schema = @Schema(implementation = ServiceOperationResource.class))),
            @ApiResponse(responseCode = "403", description = "User is not the machinery owner"),
            @ApiResponse(responseCode = "404", description = "Operation or worked hours record not found"),
            @ApiResponse(responseCode = "422", description = "Record not in PENDING status")
    })
    public ResponseEntity<?> updateWorkedHoursStatus(@PathVariable Long operationId,
                                                     @PathVariable Long workedHoursId,
                                                     @Valid @RequestBody UpdateWorkedHoursStatusResource resource,
                                                     Authentication authentication) {
        var validatorProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        Result<ServiceOperation, ApplicationError> result;
        if ("VALIDATED".equalsIgnoreCase(resource.status())) {
            result = operationsManagementCommandService.handle(
                    new ValidateWorkedHoursCommand(operationId, workedHoursId, validatorProfileId));
        } else if ("REJECTED".equalsIgnoreCase(resource.status())) {
            result = operationsManagementCommandService.handle(
                    new RejectWorkedHoursCommand(operationId, workedHoursId, validatorProfileId));
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                ServiceOperationResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    @PatchMapping("/{operationId}/status")
    @Operation(summary = "Complete operation",
            description = "Fleet owner completes the operation when no shifts remain pending validation.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Operation completed",
                    content = @Content(schema = @Schema(implementation = ServiceOperationResource.class))),
            @ApiResponse(responseCode = "403", description = "User is not the machinery owner"),
            @ApiResponse(responseCode = "404", description = "Operation not found"),
            @ApiResponse(responseCode = "422", description = "Worked hours still pending or operation not IN_PROGRESS")
    })
    public ResponseEntity<?> updateOperationStatus(@PathVariable Long operationId,
                                                   @Valid @RequestBody UpdateOperationStatusResource resource,
                                                   Authentication authentication) {
        var ownerProfileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        if (!"COMPLETED".equalsIgnoreCase(resource.status())) {
            return ResponseEntity.badRequest().build();
        }
        var command = new CompleteOperationCommand(operationId, ownerProfileId);
        return ResponseEntityAssembler.toResponseEntityFromResult(
                operationsManagementCommandService.handle(command),
                ServiceOperationResourceFromEntityAssembler::toResourceFromEntity,
                HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Query service operations", description = "Filter operations by rentalId or machineryId, or list user operations.")
    public ResponseEntity<List<ServiceOperationResource>> getOperations(
            @RequestParam(required = false) Long rentalId,
            @RequestParam(required = false) Long machineryId,
            Authentication authentication) {
        if (rentalId != null) {
            var opOpt = operationsManagementQueryService.handle(new ViewServiceOperationByRentalIdQuery(rentalId));
            var list = opOpt.map(op -> List.of(ServiceOperationResourceFromEntityAssembler.toResourceFromEntity(op)))
                    .orElse(List.of());
            return ResponseEntity.ok(list);
        }
        if (machineryId != null) {
            var list = operationsManagementQueryService.handle(new GetOperationsByMachineryIdQuery(machineryId)).stream()
                    .map(ServiceOperationResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();
            return ResponseEntity.ok(list);
        }
        var profileId = profilesContextFacade.fetchProfileIdByUserEmail(authentication.getName());
        var list = operationsManagementQueryService.handle(new GetOperationsByOperatorProfileIdQuery(profileId)).stream()
                .map(ServiceOperationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{operationId}")
    @Operation(summary = "Get service operation by ID")
    public ResponseEntity<ServiceOperationResource> getOperationById(@PathVariable Long operationId) {
        return operationsManagementQueryService.handle(new ViewServiceOperationQuery(operationId))
                .map(ServiceOperationResourceFromEntityAssembler::toResourceFromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{operationId}/worked-hours")
    @Operation(summary = "Get worked hours of an operation")
    public ResponseEntity<List<WorkedHoursResource>> getWorkedHours(@PathVariable Long operationId) {
        var list = operationsManagementQueryService.handle(new ViewWorkedHoursQuery(operationId)).stream()
                .map(ServiceOperationResourceFromEntityAssembler::toResourceFromEntity)
                .toList();
        return ResponseEntity.ok(list);
    }
}
