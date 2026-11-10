package com.andesheavytech.maquicontrol.platform.billing.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

/**
 * Body of {@code PATCH /api/v1/invoices/{invoiceId}/status}: ISSUED submits the invoice to SUNAT.
 */
public record UpdateInvoiceStatusResource(@NotBlank String status) {
}
