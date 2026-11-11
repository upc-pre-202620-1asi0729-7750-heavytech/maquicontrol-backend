package com.andesheavytech.maquicontrol.platform.billing.infrastructure.persistence.jpa.adapters;

import com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates.Invoice;
import com.andesheavytech.maquicontrol.platform.billing.domain.repositories.InvoiceRepository;
import com.andesheavytech.maquicontrol.platform.billing.infrastructure.persistence.jpa.assemblers.InvoicePersistenceAssembler;
import com.andesheavytech.maquicontrol.platform.billing.infrastructure.persistence.jpa.entities.InvoicePersistenceEntity;
import com.andesheavytech.maquicontrol.platform.billing.infrastructure.persistence.jpa.repositories.InvoicePersistenceRepository;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Adapter that implements {@link InvoiceRepository} with Spring Data JPA.
 */
@Repository
public class InvoiceRepositoryImpl implements InvoiceRepository {

    private final InvoicePersistenceRepository invoicePersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public InvoiceRepositoryImpl(InvoicePersistenceRepository invoicePersistenceRepository,
                                 DomainEventPublisher domainEventPublisher) {
        this.invoicePersistenceRepository = invoicePersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Invoice save(Invoice invoice) {
        var isNew = invoice.getId() == null;
        var entity = isNew
                ? new InvoicePersistenceEntity()
                : invoicePersistenceRepository.findById(invoice.getId())
                        .orElseThrow(() -> new IllegalArgumentException("invoice %d does not exist".formatted(invoice.getId())));

        var saved = invoicePersistenceRepository.saveAndFlush(
                InvoicePersistenceAssembler.copyToPersistence(invoice, entity));
        var savedInvoice = InvoicePersistenceAssembler.toDomainFromPersistence(saved);

        if (isNew) {
            savedInvoice.onCalculated();
            domainEventPublisher.publishAndClearEvents(savedInvoice);
        } else {
            domainEventPublisher.publishAndClearEvents(invoice);
        }
        return savedInvoice;
    }

    @Override
    public Optional<Invoice> findById(Long id) {
        return invoicePersistenceRepository.findById(id)
                .map(InvoicePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Invoice> findByRentalId(Long rentalId) {
        return invoicePersistenceRepository.findByRentalId(rentalId)
                .map(InvoicePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Invoice> findByRentalIdIn(Collection<Long> rentalIds) {
        if (rentalIds == null || rentalIds.isEmpty()) {
            return List.of();
        }
        return invoicePersistenceRepository.findByRentalIdIn(rentalIds).stream()
                .map(InvoicePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public List<Invoice> findAll() {
        return invoicePersistenceRepository.findAll().stream()
                .map(InvoicePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public boolean existsByRentalId(Long rentalId) {
        return invoicePersistenceRepository.existsByRentalId(rentalId);
    }
}
