package com.andesheavytech.maquicontrol.platform.billing.application.internal.eventhandlers;

import com.andesheavytech.maquicontrol.platform.billing.domain.model.events.InvoiceIssuedEvent;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.events.LatePenaltyAppliedEvent;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.events.RentalAmountCalculatedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * Handles domain events produced within the Billing Management bounded context.
 */
@Service
@Slf4j
public class BillingDomainEventHandler {

    @EventListener
    public void on(RentalAmountCalculatedEvent event) {
        log.info("Invoice calculated for rental {}: {} {}", event.rentalId(), event.amount(), event.currency());
    }

    @EventListener
    public void on(InvoiceIssuedEvent event) {
        log.info("Invoice issued: {} for rental {} (Invoice Number: {})",
                event.invoiceId(), event.rentalId(), event.invoiceNumber());
    }

    @EventListener
    public void on(LatePenaltyAppliedEvent event) {
        log.info("Late penalty {} applied to rental {}: {} {} (Reason: {})",
                event.penaltyId(), event.rentalId(), event.amount(), event.currency(), event.reason());
    }
}
