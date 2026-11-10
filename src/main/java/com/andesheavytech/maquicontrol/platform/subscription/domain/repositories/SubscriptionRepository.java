package com.andesheavytech.maquicontrol.platform.subscription.domain.repositories;

import com.andesheavytech.maquicontrol.platform.subscription.domain.model.aggregates.Subscription;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for {@link Subscription}.
 */
public interface SubscriptionRepository {

    Subscription save(Subscription subscription);

    Optional<Subscription> findById(Long id);

    Optional<Subscription> findByOwnerProfileId(Long ownerProfileId);

    List<Subscription> findAllByOwnerProfileId(Long ownerProfileId);

    Optional<Subscription> findActiveByOwnerProfileId(Long ownerProfileId);

    boolean existsActiveByOwnerProfileId(Long ownerProfileId);
}
