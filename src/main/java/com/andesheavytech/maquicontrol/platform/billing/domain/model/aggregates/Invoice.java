package com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates;

import com.andesheavytech.maquicontrol.platform.billing.domain.model.events.InvoiceIssuedEvent;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.events.RentalAmountCalculatedEvent;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.valueobjects.InvoiceNumber;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.valueobjects.InvoiceStatus;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.valueobjects.SunatCdr;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Invoice aggregate root: tax electronic invoice issued to SUNAT for a completed rental.
 */
@Getter
public class Invoice extends AbstractDomainAggregateRoot<Invoice> {

    private final Long id;
    private final Long rentalId;
    private InvoiceNumber invoiceNumber;
    private SunatCdr sunatCdr;
    private final Money amount;
    private InvoiceStatus status;
    private LocalDateTime issuedAt;
    private String rejectionReason;

    private Invoice(Long id, Long rentalId, InvoiceNumber invoiceNumber, SunatCdr sunatCdr,
                    Money amount, InvoiceStatus status, LocalDateTime issuedAt, String rejectionReason) {
        if (rentalId == null || rentalId <= 0) {
            throw new IllegalArgumentException("rentalId is required");
        }
        this.id = id;
        this.rentalId = rentalId;
        this.invoiceNumber = invoiceNumber;
        this.sunatCdr = sunatCdr;
        this.amount = Objects.requireNonNull(amount, "amount is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.issuedAt = issuedAt;
        this.rejectionReason = rejectionReason;
    }

    /**
     * Calculates the amount and creates a pending invoice.
     */
    public static Invoice calculate(Long rentalId, Money amount) {
        return new Invoice(null, rentalId, null, null, amount, InvoiceStatus.PENDING, null, null);
    }

    public static Invoice reconstitute(Long id, Long rentalId, InvoiceNumber invoiceNumber, SunatCdr sunatCdr,
                                       Money amount, InvoiceStatus status, LocalDateTime issuedAt,
                                       String rejectionReason) {
        return new Invoice(id, rentalId, invoiceNumber, sunatCdr, amount, status, issuedAt, rejectionReason);
    }

    /**
     * Registers {@link RentalAmountCalculatedEvent} after persistence assigns the ID.
     */
    public void onCalculated() {
        registerDomainEvent(new RentalAmountCalculatedEvent(id, rentalId, amount.amount(), amount.currency().name()));
    }

    /**
     * Issues the invoice to SUNAT with a series/correlative and acceptance CDR.
     */
    public void issue(InvoiceNumber invoiceNumber, SunatCdr sunatCdr) {
        if (status != InvoiceStatus.PENDING) {
            throw new IllegalStateException("cannot issue an invoice that is %s".formatted(status));
        }
        this.invoiceNumber = Objects.requireNonNull(invoiceNumber, "invoiceNumber is required");
        this.sunatCdr = Objects.requireNonNull(sunatCdr, "sunatCdr is required");
        this.status = InvoiceStatus.ISSUED;
        this.issuedAt = LocalDateTime.now();
        registerDomainEvent(new InvoiceIssuedEvent(id, rentalId, invoiceNumber.format()));
    }

    /**
     * Rejection by SUNAT or validation failure.
     */
    public void reject(String reason) {
        this.status = InvoiceStatus.REJECTED;
        this.rejectionReason = reason;
    }

    public boolean isIssued() {
        return status == InvoiceStatus.ISSUED;
    }
}
