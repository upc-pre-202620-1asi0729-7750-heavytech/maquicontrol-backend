package com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.resources;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representation of an electronic invoice resource.
 */
public record InvoiceResource(
        Long id,
        Long rentalId,
        String invoiceNumber,
        String sunatCdr,
        BigDecimal amount,
        String currency,
        String status,
        LocalDateTime issuedAt,
        String rejectionReason
) {
}
