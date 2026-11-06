package com.andesheavytech.maquicontrol.platform.shared.infrastructure.outbox.persistence.jpa.repositories;

import com.andesheavytech.maquicontrol.platform.shared.infrastructure.outbox.persistence.jpa.entities.OutboxEventPersistenceEntity;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.outbox.persistence.jpa.entities.OutboxEventStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data repository for the transactional outbox.
 */
@Repository
public interface OutboxEventPersistenceRepository extends JpaRepository<OutboxEventPersistenceEntity, Long> {

    /**
     * Finds the oldest events in the given status, oldest first, so events are relayed in order.
     */
    List<OutboxEventPersistenceEntity> findByStatusOrderByIdAsc(OutboxEventStatus status, Pageable pageable);
}
