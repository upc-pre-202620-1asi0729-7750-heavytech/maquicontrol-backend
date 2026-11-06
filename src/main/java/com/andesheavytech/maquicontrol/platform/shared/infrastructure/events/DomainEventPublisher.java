package com.andesheavytech.maquicontrol.platform.shared.infrastructure.events;

import com.andesheavytech.maquicontrol.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.outbox.OutboxEventRecorder;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Shared infrastructure service responsible for publishing and clearing domain events
 * registered on {@link AbstractDomainAggregateRoot} instances.
 *
 * <p>Events are not dispatched immediately: they are written to the transactional outbox in the
 * same transaction as the aggregate, and the {@code OutboxEventRelay} delivers them to the
 * {@code @EventListener} handlers once the transaction has committed (see ADR-0008).</p>
 */
@Component
@NullMarked
public class DomainEventPublisher {

    private final OutboxEventRecorder outboxEventRecorder;

    /**
     * Constructs a new {@link DomainEventPublisher}.
     *
     * @param outboxEventRecorder the recorder that writes events into the outbox
     */
    public DomainEventPublisher(OutboxEventRecorder outboxEventRecorder) {
        this.outboxEventRecorder = outboxEventRecorder;
    }

    /**
     * Stores all domain events registered on the aggregate root in the outbox and clears the
     * aggregate's internal event collection.
     *
     * @param aggregate the domain aggregate root whose events are to be published
     * @param <T> the aggregate root type
     */
    @Transactional
    public <T extends AbstractDomainAggregateRoot<T>> void publishAndClearEvents(@Nullable T aggregate) {
        if (aggregate == null) {
            return;
        }
        var aggregateType = aggregate.getClass().getSimpleName();
        aggregate.domainEvents().forEach(event -> outboxEventRecorder.record(aggregateType, event));
        aggregate.clearDomainEvents();
    }
}
