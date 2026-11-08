package com.andesheavytech.maquicontrol.platform.billing.domain.model.aggregates;

import com.andesheavytech.maquicontrol.platform.billing.domain.model.events.LatePenaltyAppliedEvent;
import com.andesheavytech.maquicontrol.platform.billing.domain.model.valueobjects.Reason;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * LatePenalty aggregate root: applies a financial surcharge when machinery is returned after the agreed rental end date.
 */
@Getter
public class LatePenalty extends AbstractDomainAggregateRoot<LatePenalty> {

    private final Long id;
    private final Long rentalId;
    private final Money amount;
    private final Reason reason;
    private final LocalDateTime appliedAt;

    private LatePenalty(Long id, Long rentalId, Money amount, Reason reason, LocalDateTime appliedAt) {
        if (rentalId == null || rentalId <= 0) {
            throw new IllegalArgumentException("rentalId is required");
        }
        this.id = id;
        this.rentalId = rentalId;
        this.amount = Objects.requireNonNull(amount, "amount is required");
        this.reason = Objects.requireNonNull(reason, "reason is required");
        this.appliedAt = Objects.requireNonNull(appliedAt, "appliedAt is required");
    }

    public static LatePenalty apply(Long rentalId, Money amount, Reason reason) {
        return new LatePenalty(null, rentalId, amount, reason, LocalDateTime.now());
    }

    public static LatePenalty reconstitute(Long id, Long rentalId, Money amount, Reason reason, LocalDateTime appliedAt) {
        return new LatePenalty(id, rentalId, amount, reason, appliedAt);
    }

    /**
     * Registers {@link LatePenaltyAppliedEvent} after persistence assigns the ID.
     */
    public void onApplied() {
        registerDomainEvent(new LatePenaltyAppliedEvent(id, rentalId, amount.amount(), amount.currency().name(), reason.value()));
    }
}
