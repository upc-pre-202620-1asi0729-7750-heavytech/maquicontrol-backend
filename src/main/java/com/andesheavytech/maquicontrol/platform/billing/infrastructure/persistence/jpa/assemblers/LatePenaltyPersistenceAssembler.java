package com.andesheavytech.maquicontrol.platform.billing.infrastructure.persistence.jpa.assemblers;

import com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates.LatePenalty;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.valueobjects.Reason;
import com.andesheavytech.maquicontrol.platform.billing.infrastructure.persistence.jpa.entities.LatePenaltyPersistenceEntity;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;

/**
 * Assembler between {@link LatePenalty} and {@link LatePenaltyPersistenceEntity}.
 */
public final class LatePenaltyPersistenceAssembler {

    private LatePenaltyPersistenceAssembler() {
    }

    public static LatePenalty toDomainFromPersistence(LatePenaltyPersistenceEntity entity) {
        Money amount = new Money(entity.getAmount(), entity.getCurrency());
        Reason reason = new Reason(entity.getReason());

        return LatePenalty.reconstitute(
                entity.getId(),
                entity.getRentalId(),
                amount,
                reason,
                entity.getAppliedAt()
        );
    }

    public static LatePenaltyPersistenceEntity copyToPersistence(LatePenalty penalty, LatePenaltyPersistenceEntity entity) {
        entity.setRentalId(penalty.getRentalId());
        entity.setAmount(penalty.getAmount().amount());
        entity.setCurrency(penalty.getAmount().currency().name());
        entity.setReason(penalty.getReason().value());
        entity.setAppliedAt(penalty.getAppliedAt());
        return entity;
    }
}
