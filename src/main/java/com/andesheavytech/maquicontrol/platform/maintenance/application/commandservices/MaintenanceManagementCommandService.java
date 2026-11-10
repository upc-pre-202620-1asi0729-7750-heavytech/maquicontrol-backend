package com.andesheavytech.maquicontrol.platform.maintenance.application.commandservices;

import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.aggregates.Maintenance;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.CancelMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.CompleteMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.ReportBreakdownCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.ScheduleMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.StartMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;

/**
 * Command service for the Maintenance Management bounded context.
 */
public interface MaintenanceManagementCommandService {

    Result<Maintenance, ApplicationError> handle(ScheduleMaintenanceCommand command);

    Result<Maintenance, ApplicationError> handle(StartMaintenanceCommand command);

    Result<Maintenance, ApplicationError> handle(CancelMaintenanceCommand command);

    Result<Maintenance, ApplicationError> handle(CompleteMaintenanceCommand command);

    Result<Maintenance, ApplicationError> handle(ReportBreakdownCommand command);
}
