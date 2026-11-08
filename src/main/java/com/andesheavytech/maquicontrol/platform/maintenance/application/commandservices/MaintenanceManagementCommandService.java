package com.andesheavytech.maquicontrol.platform.maintenance.application.commandservices;

import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.aggregates.Maintenance;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.CompleteMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.ReportBreakdownCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.ScheduleMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands.UpdateMaintenanceStatusCommand;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;

/**
 * Command service for the Maintenance Management bounded context.
 */
public interface MaintenanceManagementCommandService {

    Result<Maintenance, ApplicationError> handle(ScheduleMaintenanceCommand command);

    Result<Maintenance, ApplicationError> handle(UpdateMaintenanceStatusCommand command);

    Result<Maintenance, ApplicationError> handle(CompleteMaintenanceCommand command);

    Result<Maintenance, ApplicationError> handle(ReportBreakdownCommand command);
}
