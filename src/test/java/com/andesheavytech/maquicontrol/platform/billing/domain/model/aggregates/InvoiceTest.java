package com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates;

import com.andesheavytech.maquicontrol.platform.billing.domain.model.valueobjects.InvoiceNumber;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.valueobjects.InvoiceStatus;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.valueobjects.SunatCdr;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InvoiceTest {

    @Test
    void calculateCreatesPendingInvoice() {
        var invoice = Invoice.calculate(1L, new Money(new BigDecimal("1500.00"), "PEN"));
        assertEquals(1L, invoice.getRentalId());
        assertEquals(new BigDecimal("1500.00"), invoice.getAmount().amount());
        assertEquals(InvoiceStatus.PENDING, invoice.getStatus());
        assertFalse(invoice.isIssued());
    }

    @Test
    void calculateRequiresRentalIdAndAmount() {
        assertThrows(IllegalArgumentException.class, () -> Invoice.calculate(null, new Money(BigDecimal.TEN, "PEN")));
        assertThrows(NullPointerException.class, () -> Invoice.calculate(1L, null));
    }

    @Test
    void issueTransitionsToIssuedAndAssignsNumberAndCdr() {
        var invoice = Invoice.calculate(1L, new Money(new BigDecimal("1500.00"), "PEN"));
        var invoiceNumber = new InvoiceNumber("F001", 101);
        var cdr = new SunatCdr("CDR-ACCEPTED-F001-00000101-SUNAT-OK");

        invoice.issue(invoiceNumber, cdr);

        assertEquals(InvoiceStatus.ISSUED, invoice.getStatus());
        assertTrue(invoice.isIssued());
        assertEquals("F001-00000101", invoice.getInvoiceNumber().format());
        assertEquals(cdr, invoice.getSunatCdr());
        assertNotNull(invoice.getIssuedAt());
    }

    @Test
    void issueFailsIfNotPending() {
        var invoice = Invoice.calculate(1L, new Money(new BigDecimal("1500.00"), "PEN"));
        invoice.issue(new InvoiceNumber("F001", 1), new SunatCdr("CDR-1"));
        assertThrows(IllegalStateException.class, () -> invoice.issue(new InvoiceNumber("F001", 2), new SunatCdr("CDR-2")));
    }

    @Test
    void rejectSetsStatusToRejectedWithReason() {
        var invoice = Invoice.calculate(1L, new Money(new BigDecimal("1500.00"), "PEN"));
        invoice.reject("RUC is invalid according to SUNAT");

        assertEquals(InvoiceStatus.REJECTED, invoice.getStatus());
        assertEquals("RUC is invalid according to SUNAT", invoice.getRejectionReason());
    }
}
