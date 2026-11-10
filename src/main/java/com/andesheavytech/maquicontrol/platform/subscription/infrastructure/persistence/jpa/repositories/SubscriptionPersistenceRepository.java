package com.andesheavytech.maquicontrol.platform.subscription.infrastructure.persistence.jpa.repositories;

import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.SubscriptionStatus;
import com.andesheavytech.maquicontrol.platform.subscription.infrastructure.persistence.jpa.entities.SubscriptionPersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link SubscriptionPersistenceEntity}.
 */
@Repository
public interface SubscriptionPersistenceRepository extends JpaRepository<SubscriptionPersistenceEntity, Long> {

    Optional<SubscriptionPersistenceEntity> findFirstByOwnerProfileIdOrderByCreatedAtDesc(Long ownerProfileId);

    List<SubscriptionPersistenceEntity> findByOwnerProfileIdOrderByCreatedAtDesc(Long ownerProfileId);

    Optional<SubscriptionPersistenceEntity> findFirstByOwnerProfileIdAndStatusOrderByCreatedAtDesc(
            Long ownerProfileId, SubscriptionStatus status);

    boolean existsByOwnerProfileIdAndStatus(Long ownerProfileId, SubscriptionStatus status);
}
