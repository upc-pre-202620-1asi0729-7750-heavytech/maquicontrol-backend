package com.andesheavytech.maquicontrol.platform.shared.infrastructure.outbox;

import com.andesheavytech.maquicontrol.platform.shared.infrastructure.outbox.persistence.jpa.entities.OutboxEventPersistenceEntity;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.outbox.persistence.jpa.repositories.OutboxEventPersistenceRepository;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.json.JsonMapper;

/**
 * Writes domain events into the transactional outbox.
 *
 * <p>Joins the caller's transaction, so the event row commits or rolls back together with the
 * aggregate that registered it.</p>
 */
@Component
@NullMarked
public class OutboxEventRecorder {

    private final OutboxEventPersistenceRepository outboxEventPersistenceRepository;
    private final JsonMapper jsonMapper;

    public OutboxEventRecorder(OutboxEventPersistenceRepository outboxEventPersistenceRepository,
                               JsonMapper jsonMapper) {
        this.outboxEventPersistenceRepository = outboxEventPersistenceRepository;
        this.jsonMapper = jsonMapper;
    }

    /**
     * Serializes the event and stores it as a pending outbox row.
     *
     * @param aggregateType the simple name of the aggregate that registered the event
     * @param event         the domain event
     */
    @Transactional
    public void record(String aggregateType, Object event) {
        var payload = jsonMapper.writeValueAsString(event);
        outboxEventPersistenceRepository.save(
                new OutboxEventPersistenceEntity(aggregateType, event.getClass().getName(), payload));
    }
}
