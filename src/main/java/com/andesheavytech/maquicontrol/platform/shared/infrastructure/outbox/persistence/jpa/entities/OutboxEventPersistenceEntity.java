package com.andesheavytech.maquicontrol.platform.shared.infrastructure.outbox.persistence.jpa.entities;

import com.andesheavytech.maquicontrol.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * Row of the transactional outbox.
 *
 * <p>Every domain event an aggregate registers is stored here, serialized as JSON, inside the
 * same database transaction that saves the aggregate. The {@code OutboxEventRelay} later reads
 * pending rows and publishes them, so an event is never lost when the aggregate is saved, and
 * never published when the save is rolled back.</p>
 */
@Getter
@Entity
@Table(name = "outbox_events", indexes = @Index(name = "idx_outbox_events_status", columnList = "status"))
@NoArgsConstructor
public class OutboxEventPersistenceEntity extends AuditableAbstractPersistenceEntity {

    @Column(nullable = false, length = 100)
    private String aggregateType;

    @Column(nullable = false)
    private String eventType;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OutboxEventStatus status;

    @Column(nullable = false)
    private int attempts;

    @Column(length = 1000)
    private String lastError;

    private Date publishedAt;

    public OutboxEventPersistenceEntity(String aggregateType, String eventType, String payload) {
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.payload = payload;
        this.status = OutboxEventStatus.PENDING;
        this.attempts = 0;
    }

    /** Marks the event as delivered. */
    public void markAsPublished() {
        this.status = OutboxEventStatus.PUBLISHED;
        this.publishedAt = new Date();
        this.lastError = null;
    }

    /**
     * Records a failed delivery. After {@code maxAttempts} the event stops being retried.
     *
     * @param error       the failure message
     * @param maxAttempts the maximum number of delivery attempts
     */
    public void markAsFailedAttempt(String error, int maxAttempts) {
        this.attempts++;
        this.lastError = error != null && error.length() > 1000 ? error.substring(0, 1000) : error;
        if (this.attempts >= maxAttempts) {
            this.status = OutboxEventStatus.FAILED;
        }
    }
}
