package com.andesheavytech.maquicontrol.platform.billing.domain.model.events;

import java.math.BigDecimal;

/**
 * Domain event published when rental amount is calculated for an invoice.
 */
public record RentalAmountCalculatedEvent(Long invoiceId, Long rentalId, BigDecimal amount, String currency) {
}
