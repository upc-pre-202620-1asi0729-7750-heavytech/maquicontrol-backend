package com.andesheavytech.maquicontrol.platform.billing.application.internal.commandservices;

import com.andesheavytech.maquicontrol.platform.billing.application.commandservices.BillingManagementCommandService;
import com.andesheavytech.maquicontrol.platform.billing.application.internal.outboundservices.acl.ExternalFleetService;
import com.andesheavytech.maquicontrol.platform.billing.application.internal.outboundservices.acl.ExternalRentalService;
import com.andesheavytech.maquicontrol.platform.billing.application.internal.outboundservices.acl.SunatService;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates.Invoice;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates.LatePenalty;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.commands.ApplyLatePenaltyCommand;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.commands.CalculateRentalAmountCommand;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.commands.IssueInvoiceCommand;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.valueobjects.Reason;
import com.andesheavytech.maquicontrol.platform.billing.domain.repositories.InvoiceRepository;
import com.andesheavytech.maquicontrol.platform.billing.domain.repositories.LatePenaltyRepository;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link BillingManagementCommandService}.
 */
@Service
public class BillingManagementCommandServiceImpl implements BillingManagementCommandService {

    private final InvoiceRepository invoiceRepository;
    private final LatePenaltyRepository latePenaltyRepository;
    private final SunatService sunatService;
    private final ExternalRentalService externalRentalService;
    private final ExternalFleetService externalFleetService;

    public BillingManagementCommandServiceImpl(InvoiceRepository invoiceRepository,
                                                LatePenaltyRepository latePenaltyRepository,
                                                SunatService sunatService,
                                                @Qualifier("billingExternalRentalService") ExternalRentalService externalRentalService,
                                                @Qualifier("billingExternalFleetService") ExternalFleetService externalFleetService) {
        this.invoiceRepository = invoiceRepository;
        this.latePenaltyRepository = latePenaltyRepository;
        this.sunatService = sunatService;
        this.externalRentalService = externalRentalService;
        this.externalFleetService = externalFleetService;
    }

    @Override
    @Transactional
    public Result<Invoice, ApplicationError> handle(CalculateRentalAmountCommand command) {
        if (!externalRentalService.existsRental(command.rentalId())) {
            return Result.failure(ApplicationError.notFound("Rental", String.valueOf(command.rentalId())));
        }
        var existing = invoiceRepository.findByRentalId(command.rentalId());
        if (existing.isPresent()) {
            return Result.success(existing.get());
        }
        try {
            var invoice = Invoice.calculate(command.rentalId(), command.amount());
            return Result.success(invoiceRepository.save(invoice));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("invoice", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Invoice, ApplicationError> handle(IssueInvoiceCommand command) {
        var found = invoiceRepository.findById(command.invoiceId());
        if (found.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Invoice", String.valueOf(command.invoiceId())));
        }
        var invoice = found.get();
        if (command.ownerProfileId() != null && command.ownerProfileId() > 0) {
            Long machineryId = externalRentalService.fetchRentalMachineryId(invoice.getRentalId());
            var ownerIdOpt = externalFleetService.fetchOwnerProfileId(machineryId);
            if (ownerIdOpt.isPresent() && !ownerIdOpt.get().equals(command.ownerProfileId())) {
                return Result.failure(ApplicationError.forbidden("invoice", "only the fleet owner can issue this invoice"));
            }
        }
        try {
            var sunatResult = sunatService.issueElectronicInvoice(invoice.getRentalId(), invoice.getAmount());
            invoice.issue(sunatResult.invoiceNumber(), sunatResult.cdr());
            return Result.success(invoiceRepository.save(invoice));
        } catch (IllegalStateException e) {
            return Result.failure(ApplicationError.businessRuleViolation("invoice status", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<LatePenalty, ApplicationError> handle(ApplyLatePenaltyCommand command) {
        if (!externalRentalService.existsRental(command.rentalId())) {
            return Result.failure(ApplicationError.notFound("Rental", String.valueOf(command.rentalId())));
        }
        try {
            var penalty = LatePenalty.apply(command.rentalId(), command.amount(), new Reason(command.reason()));
            return Result.success(latePenaltyRepository.save(penalty));
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("late penalty", e.getMessage()));
        }
    }
}
