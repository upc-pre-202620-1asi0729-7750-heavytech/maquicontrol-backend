package com.andesheavytech.maquicontrol.platform.billing.infrastructure.persistence.jpa.assemblers;

import com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates.Invoice;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.valueobjects.InvoiceNumber;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.valueobjects.SunatCdr;
import com.andesheavytech.maquicontrol.platform.billing.infrastructure.persistence.jpa.entities.InvoicePersistenceEntity;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;

/**
 * Assembler between {@link Invoice} and {@link InvoicePersistenceEntity}.
 */
public final class InvoicePersistenceAssembler {

    private InvoicePersistenceAssembler() {
    }

    public static Invoice toDomainFromPersistence(InvoicePersistenceEntity entity) {
        InvoiceNumber invoiceNumber = (entity.getSeries() != null && entity.getCorrelative() != null)
                ? new InvoiceNumber(entity.getSeries(), entity.getCorrelative())
                : null;
        SunatCdr sunatCdr = entity.getSunatCdr() != null ? new SunatCdr(entity.getSunatCdr()) : null;
        Money amount = new Money(entity.getAmount(), entity.getCurrency());

        return Invoice.reconstitute(
                entity.getId(),
                entity.getRentalId(),
                invoiceNumber,
                sunatCdr,
                amount,
                entity.getStatus(),
                entity.getIssuedAt(),
                entity.getRejectionReason()
        );
    }

    public static InvoicePersistenceEntity copyToPersistence(Invoice invoice, InvoicePersistenceEntity entity) {
        entity.setRentalId(invoice.getRentalId());
        if (invoice.getInvoiceNumber() != null) {
            entity.setSeries(invoice.getInvoiceNumber().series());
            entity.setCorrelative(invoice.getInvoiceNumber().correlative());
        }
        if (invoice.getSunatCdr() != null) {
            entity.setSunatCdr(invoice.getSunatCdr().value());
        }
        entity.setAmount(invoice.getAmount().amount());
        entity.setCurrency(invoice.getAmount().currency().name());
        entity.setStatus(invoice.getStatus());
        entity.setIssuedAt(invoice.getIssuedAt());
        entity.setRejectionReason(invoice.getRejectionReason());
        return entity;
    }
}
