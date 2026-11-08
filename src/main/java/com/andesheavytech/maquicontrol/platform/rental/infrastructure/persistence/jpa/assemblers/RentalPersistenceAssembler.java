package com.andesheavytech.maquicontrol.platform.rental.infrastructure.persistence.jpa.assemblers;

import com.andesheavytech.maquicontrol.platform.rental.domain.model.aggregates.Rental;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects.RentalPeriod;
import com.andesheavytech.maquicontrol.platform.rental.infrastructure.persistence.jpa.entities.RentalPersistenceEntity;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;

public final class RentalPersistenceAssembler {

    private RentalPersistenceAssembler() {
    }

    public static Rental toDomainFromPersistence(RentalPersistenceEntity entity) {
        return Rental.reconstitute(entity.getId(), entity.getMachineryId(), entity.getContractorProfileId(),
                new RentalPeriod(entity.getStartDate(), entity.getEndDate()),
                new Money(entity.getTotalAmount(), entity.getTotalCurrency()),
                entity.getStatus(), entity.getCancellationReason(), entity.getRequestedAt(),
                entity.getConfirmedAt(), entity.getCancelledAt());
    }

    public static RentalPersistenceEntity toPersistenceFromDomain(Rental rental) {
        var entity = new RentalPersistenceEntity();
        if (rental.getId() != null) {
            entity.setId(rental.getId());
        }
        entity.setMachineryId(rental.getMachineryId());
        entity.setContractorProfileId(rental.getContractorProfileId());
        entity.setStatus(rental.getStatus());
        entity.setTotalAmount(rental.getTotalAmount().amount());
        entity.setTotalCurrency(rental.getTotalAmount().currency());
        entity.setCancellationReason(rental.getCancellationReason());
        entity.setRequestedAt(rental.getRequestedAt());
        entity.setConfirmedAt(rental.getConfirmedAt());
        entity.setCancelledAt(rental.getCancelledAt());
        entity.setStartDate(rental.getPeriod().startDate());
        entity.setEndDate(rental.getPeriod().endDate());
        return entity;
    }
}
