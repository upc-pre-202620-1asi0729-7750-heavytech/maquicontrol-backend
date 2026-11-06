package com.andesheavytech.maquicontrol.platform.shared.infrastructure.outbox;

import com.andesheavytech.maquicontrol.platform.shared.infrastructure.outbox.persistence.jpa.entities.OutboxEventPersistenceEntity;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.outbox.persistence.jpa.entities.OutboxEventStatus;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.outbox.persistence.jpa.repositories.OutboxEventPersistenceRepository;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.json.JsonMapper;

/**
 * Relays pending outbox events to the in-process event handlers.
 *
 * <p>Runs on a fixed delay. Each event is delivered in its own transaction: the handlers run
 * and the row is marked {@code PUBLISHED} together, so a handler failure rolls back only that
 * event, which is retried on the next run until {@code outbox.relay.max-attempts} is reached.
 * Handlers must therefore be idempotent.</p>
 */
@Component
@NullMarked
public class OutboxEventRelay {

    private static final Logger LOGGER = LoggerFactory.getLogger(OutboxEventRelay.class);

    private final OutboxEventPersistenceRepository outboxEventPersistenceRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final JsonMapper jsonMapper;
    private final TransactionTemplate transactionTemplate;
    private final int batchSize;
    private final int maxAttempts;

    public OutboxEventRelay(OutboxEventPersistenceRepository outboxEventPersistenceRepository,
                            ApplicationEventPublisher applicationEventPublisher,
                            JsonMapper jsonMapper,
                            PlatformTransactionManager transactionManager,
                            @Value("${outbox.relay.batch-size:50}") int batchSize,
                            @Value("${outbox.relay.max-attempts:5}") int maxAttempts) {
        this.outboxEventPersistenceRepository = outboxEventPersistenceRepository;
        this.applicationEventPublisher = applicationEventPublisher;
        this.jsonMapper = jsonMapper;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.batchSize = batchSize;
        this.maxAttempts = maxAttempts;
    }

    /**
     * Publishes the oldest pending events.
     */
    @Scheduled(fixedDelayString = "${outbox.relay.fixed-delay-ms:2000}")
    public void relayPendingEvents() {
        var pendingEvents = outboxEventPersistenceRepository
                .findByStatusOrderByIdAsc(OutboxEventStatus.PENDING, PageRequest.of(0, batchSize));
        pendingEvents.forEach(this::relay);
    }

    private void relay(OutboxEventPersistenceEntity outboxEvent) {
        try {
            transactionTemplate.executeWithoutResult(status -> {
                var event = toDomainEvent(outboxEvent);
                applicationEventPublisher.publishEvent(event);
                var managed = outboxEventPersistenceRepository.findById(outboxEvent.getId()).orElseThrow();
                managed.markAsPublished();
                outboxEventPersistenceRepository.save(managed);
            });
        } catch (RuntimeException ex) {
            LOGGER.warn("Outbox event {} ({}) could not be published: {}",
                    outboxEvent.getId(), outboxEvent.getEventType(), ex.getMessage());
            transactionTemplate.executeWithoutResult(status ->
                    outboxEventPersistenceRepository.findById(outboxEvent.getId()).ifPresent(managed -> {
                        managed.markAsFailedAttempt(ex.getMessage(), maxAttempts);
                        outboxEventPersistenceRepository.save(managed);
                    }));
        }
    }

    private Object toDomainEvent(OutboxEventPersistenceEntity outboxEvent) {
        try {
            var eventClass = Class.forName(outboxEvent.getEventType());
            return jsonMapper.readValue(outboxEvent.getPayload(), eventClass);
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Unknown event type: " + outboxEvent.getEventType(), ex);
        }
    }
}
