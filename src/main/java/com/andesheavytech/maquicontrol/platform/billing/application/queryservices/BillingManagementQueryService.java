package com.andesheavytech.maquicontrol.platform.billing.application.queryservices;

import com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates.Invoice;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates.LatePenalty;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.GetLatePenaltyByIdQuery;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.ViewInvoiceByRentalIdQuery;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.ViewInvoiceHistoryQuery;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.ViewInvoiceQuery;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.ViewInvoicesByOwnerProfileIdQuery;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.queries.ViewLatePenaltiesQuery;

import java.util.List;
import java.util.Optional;

/**
 * Query service port for Billing Management.
 */
public interface BillingManagementQueryService {

    Optional<Invoice> handle(ViewInvoiceQuery query);

    Optional<Invoice> handle(ViewInvoiceByRentalIdQuery query);

    List<Invoice> handle(ViewInvoiceHistoryQuery query);

    List<Invoice> handle(ViewInvoicesByOwnerProfileIdQuery query);

    List<LatePenalty> handle(ViewLatePenaltiesQuery query);

    Optional<LatePenalty> handle(GetLatePenaltyByIdQuery query);
}
