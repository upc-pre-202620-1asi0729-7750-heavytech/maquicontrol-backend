package com.andesheavytech.maquicontrol.platform.billing.application.commandservices;

import com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates.Invoice;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates.LatePenalty;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.commands.ApplyLatePenaltyCommand;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.commands.CalculateRentalAmountCommand;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.commands.IssueInvoiceCommand;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;

/**
 * Command service port for Billing Management.
 */
public interface BillingManagementCommandService {

    Result<Invoice, ApplicationError> handle(CalculateRentalAmountCommand command);

    Result<Invoice, ApplicationError> handle(IssueInvoiceCommand command);

    Result<LatePenalty, ApplicationError> handle(ApplyLatePenaltyCommand command);
}
