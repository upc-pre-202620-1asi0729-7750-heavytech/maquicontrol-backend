package com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates.Invoice;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates.LatePenalty;
import com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.resources.InvoiceResource;
import com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.resources.LatePenaltyResource;

/**
 * Assembler between domain entities in Billing and REST resources.
 */
public final class BillingResourceFromEntityAssembler {

    private BillingResourceFromEntityAssembler() {
    }

    public static InvoiceResource toResourceFromEntity(Invoice entity) {
        return new InvoiceResource(
                entity.getId(),
                entity.getRentalId(),
                entity.getInvoiceNumber() != null ? entity.getInvoiceNumber().format() : null,
                entity.getSunatCdr() != null ? entity.getSunatCdr().value() : null,
                entity.getAmount().amount(),
                entity.getAmount().currency().name(),
                entity.getStatus().name(),
                entity.getIssuedAt(),
                entity.getRejectionReason()
        );
    }

    public static LatePenaltyResource toResourceFromEntity(LatePenalty entity) {
        return new LatePenaltyResource(
                entity.getId(),
                entity.getRentalId(),
                entity.getAmount().amount(),
                entity.getAmount().currency().name(),
                entity.getReason().value(),
                entity.getAppliedAt()
        );
    }
}
