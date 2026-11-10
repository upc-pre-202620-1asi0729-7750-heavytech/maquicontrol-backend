package com.andesheavytech.maquicontrol.platform.billing.domain.model.commands;

public record IssueInvoiceCommand(Long invoiceId, Long ownerProfileId) {
}
