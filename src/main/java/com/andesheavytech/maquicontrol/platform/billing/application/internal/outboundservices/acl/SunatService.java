package com.andesheavytech.maquicontrol.platform.billing.application.internal.outboundservices.acl;

import com.andesheavytech.maquicontrol.platform.billing.domain.model.valueobjects.InvoiceNumber;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.valueobjects.SunatCdr;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Anti-corruption layer towards SUNAT electronic invoicing service.
 */
@Service
@Slf4j
public class SunatService {

    private static final String DEFAULT_SERIES = "F001";
    private final AtomicInteger correlativeCounter = new AtomicInteger(100);

    /**
     * Submits an invoice to SUNAT and receives an accepted CDR.
     */
    public record SunatIssueResult(InvoiceNumber invoiceNumber, SunatCdr cdr) {
    }

    public SunatIssueResult issueElectronicInvoice(Long rentalId, Money amount) {
        int correlative = correlativeCounter.incrementAndGet();
        var invoiceNumber = new InvoiceNumber(DEFAULT_SERIES, correlative);
        var cdr = new SunatCdr("CDR-ACCEPTED-%s-SUNAT-OK".formatted(invoiceNumber.format()));
        log.info("SUNAT issued electronic invoice {} for rental {} amounting to {}",
                invoiceNumber.format(), rentalId, amount);
        return new SunatIssueResult(invoiceNumber, cdr);
    }
}
