package com.andesheavytech.maquicontrol.platform.operations.application.commandservices;

import com.andesheavytech.maquicontrol.platform.operations.domain.model.aggregates.ServiceOperation;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.commands.CompleteOperationCommand;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.commands.RecordWorkedHoursCommand;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.commands.RejectWorkedHoursCommand;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.commands.StartOperationCommand;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.commands.ValidateWorkedHoursCommand;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;

/**
 * Command service port for Operations Management.
 */
public interface OperationsManagementCommandService {

    Result<ServiceOperation, ApplicationError> handle(StartOperationCommand command);

    Result<ServiceOperation, ApplicationError> handle(RecordWorkedHoursCommand command);

    Result<ServiceOperation, ApplicationError> handle(ValidateWorkedHoursCommand command);

    Result<ServiceOperation, ApplicationError> handle(RejectWorkedHoursCommand command);

    Result<ServiceOperation, ApplicationError> handle(CompleteOperationCommand command);
}
