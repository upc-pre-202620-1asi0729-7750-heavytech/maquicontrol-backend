package com.andesheavytech.maquicontrol.platform.billing.domain.model.events;

/**
 * Domain event published when an invoice is issued to SUNAT.
 */
public record InvoiceIssuedEvent(Long invoiceId, Long rentalId, String invoiceNumber) {
}
