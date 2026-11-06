package com.andesheavytech.maquicontrol.platform.shared.infrastructure.outbox.persistence.jpa.entities;

/**
 * Lifecycle of a row in the transactional outbox.
 */
public enum OutboxEventStatus {
    /** Stored in the same transaction as the aggregate, waiting for the relay. */
    PENDING,
    /** Delivered to every in-process event handler. */
    PUBLISHED,
    /** Delivery kept failing until the maximum number of attempts was reached. */
    FAILED
}
