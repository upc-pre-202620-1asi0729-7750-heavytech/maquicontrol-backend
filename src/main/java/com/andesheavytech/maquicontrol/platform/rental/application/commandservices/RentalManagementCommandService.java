package com.andesheavytech.maquicontrol.platform.rental.application.commandservices;

import com.andesheavytech.maquicontrol.platform.rental.domain.model.aggregates.Rental;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.CancelRentalCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.CompleteRentalCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.ConfirmRentalCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.RejectRentalCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.RequestRentalCommand;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.commands.StartRentalCommand;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;

/**
 * Command service for the Rental Management bounded context.
 */
public interface RentalManagementCommandService {

    Result<Rental, ApplicationError> handle(RequestRentalCommand command);

    Result<Rental, ApplicationError> handle(ConfirmRentalCommand command);

    Result<Rental, ApplicationError> handle(RejectRentalCommand command);

    Result<Rental, ApplicationError> handle(CancelRentalCommand command);

    Result<Rental, ApplicationError> handle(StartRentalCommand command);

    Result<Rental, ApplicationError> handle(CompleteRentalCommand command);
}
