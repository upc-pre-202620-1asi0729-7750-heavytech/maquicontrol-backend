package com.andesheavytech.maquicontrol.platform.billing.application.internal.queryservices;

import com.andesheavytech.maquicontrol.platform.billing.application.internal.outboundservices.acl.ExternalFleetService;
import com.andesheavytech.maquicontrol.platform.billing.application.internal.outboundservices.acl.ExternalRentalService;
import com.andesheavytech.maquicontrol.platform.billing.application.queryservices.BillingManagementQueryService;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates.Invoice;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates.LatePenalty;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.GetLatePenaltyByIdQuery;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.ViewInvoiceByRentalIdQuery;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.ViewInvoiceHistoryQuery;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.ViewInvoiceQuery;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.ViewInvoicesByOwnerProfileIdQuery;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.ViewLatePenaltiesQuery;
import com.andesheavytech.maquicontrol.platform.billing.domain.repositories.InvoiceRepository;
import com.andesheavytech.maquicontrol.platform.billing.domain.repositories.LatePenaltyRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of {@link BillingManagementQueryService}.
 */
@Service
@Transactional(readOnly = true)
public class BillingManagementQueryServiceImpl implements BillingManagementQueryService {

    private final InvoiceRepository invoiceRepository;
    private final LatePenaltyRepository latePenaltyRepository;
    private final ExternalRentalService externalRentalService;
    private final ExternalFleetService externalFleetService;

    public BillingManagementQueryServiceImpl(InvoiceRepository invoiceRepository,
                                             LatePenaltyRepository latePenaltyRepository,
                                             @Qualifier("billingExternalRentalService") ExternalRentalService externalRentalService,
                                             @Qualifier("billingExternalFleetService") ExternalFleetService externalFleetService) {
        this.invoiceRepository = invoiceRepository;
        this.latePenaltyRepository = latePenaltyRepository;
        this.externalRentalService = externalRentalService;
        this.externalFleetService = externalFleetService;
    }

    @Override
    public Optional<Invoice> handle(ViewInvoiceQuery query) {
        return invoiceRepository.findById(query.invoiceId());
    }

    @Override
    public Optional<Invoice> handle(ViewInvoiceByRentalIdQuery query) {
        return invoiceRepository.findByRentalId(query.rentalId());
    }

    @Override
    public List<Invoice> handle(ViewInvoiceHistoryQuery query) {
        if (query.rentalId() != null && query.rentalId() > 0) {
            return invoiceRepository.findByRentalId(query.rentalId()).map(List::of).orElseGet(List::of);
        }
        return invoiceRepository.findAll();
    }

    @Override
    public List<Invoice> handle(ViewInvoicesByOwnerProfileIdQuery query) {
        if (query.ownerProfileId() == null || query.ownerProfileId() <= 0) {
            return List.of();
        }
        List<Long> machineryIds = externalFleetService.fetchMachineryIdsByOwnerProfileId(query.ownerProfileId());
        if (machineryIds.isEmpty()) {
            return List.of();
        }
        Set<Long> allowedMachinery = new HashSet<>(machineryIds);
        return invoiceRepository.findAll().stream()
                .filter(inv -> {
                    Long machineryId = externalRentalService.fetchRentalMachineryId(inv.getRentalId());
                    return machineryId != null && allowedMachinery.contains(machineryId);
                })
                .toList();
    }

    @Override
    public List<LatePenalty> handle(ViewLatePenaltiesQuery query) {
        if (query.rentalId() != null && query.rentalId() > 0) {
            return latePenaltyRepository.findByRentalId(query.rentalId());
        }
        return latePenaltyRepository.findAll();
    }

    @Override
    public Optional<LatePenalty> handle(GetLatePenaltyByIdQuery query) {
        return latePenaltyRepository.findById(query.penaltyId());
    }
}
