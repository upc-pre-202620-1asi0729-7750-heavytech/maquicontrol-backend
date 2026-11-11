package com.andesheavytech.maquicontrol.platform.billing.domain.repositories;

import com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates.Invoice;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for {@link Invoice}.
 */
public interface InvoiceRepository {

    Invoice save(Invoice invoice);

    Optional<Invoice> findById(Long id);

    Optional<Invoice> findByRentalId(Long rentalId);

    List<Invoice> findByRentalIdIn(Collection<Long> rentalIds);

    List<Invoice> findAll();

    boolean existsByRentalId(Long rentalId);
}
