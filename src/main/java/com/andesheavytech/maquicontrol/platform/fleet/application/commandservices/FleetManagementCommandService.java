package com.andesheavytech.maquicontrol.platform.fleet.application.commandservices;

import com.andesheavytech.maquicontrol.platform.fleet.domain.model.aggregates.Category;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.aggregates.Machinery;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.ChangeMachineryStatusCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.CreateCategoryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.DeleteCategoryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.DeleteMachineryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.FinishMachineryMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.MarkMachineryAsRentedCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.PublishMachineryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.ReleaseMachineryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.ReserveMachineryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.SeedCategoriesCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.StartMachineryMaintenanceCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.UpdateCategoryCommand;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands.UpdateMachineryCommand;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;

/**
 * Command service for the Fleet Management bounded context.
 */
public interface FleetManagementCommandService {

    Result<Machinery, ApplicationError> handle(PublishMachineryCommand command);

    Result<Machinery, ApplicationError> handle(UpdateMachineryCommand command);

    Result<Machinery, ApplicationError> handle(ChangeMachineryStatusCommand command);

    Result<Long, ApplicationError> handle(DeleteMachineryCommand command);

    Result<Machinery, ApplicationError> handle(ReserveMachineryCommand command);

    Result<Machinery, ApplicationError> handle(MarkMachineryAsRentedCommand command);

    Result<Machinery, ApplicationError> handle(ReleaseMachineryCommand command);

    Result<Machinery, ApplicationError> handle(StartMachineryMaintenanceCommand command);

    Result<Machinery, ApplicationError> handle(FinishMachineryMaintenanceCommand command);

    Result<Category, ApplicationError> handle(CreateCategoryCommand command);

    Result<Category, ApplicationError> handle(UpdateCategoryCommand command);

    Result<Long, ApplicationError> handle(DeleteCategoryCommand command);

    void handle(SeedCategoriesCommand command);
}
