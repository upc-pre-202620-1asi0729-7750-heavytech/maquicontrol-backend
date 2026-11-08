package com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence.jpa.adapters;

import com.andesheavytech.maquicontrol.platform.iam.domain.model.aggregates.UserAccount;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.EmailAddress;
import com.andesheavytech.maquicontrol.platform.iam.domain.repositories.UserAccountRepository;
import com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence.jpa.assemblers.UserAccountPersistenceAssembler;
import com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence.jpa.repositories.RolePersistenceRepository;
import com.andesheavytech.maquicontrol.platform.iam.infrastructure.persistence.jpa.repositories.UserAccountPersistenceRepository;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Adapter that implements {@link UserAccountRepository} with Spring Data JPA.
 * A new account registers its {@code UserSignedUpEvent} here, once the id is known.
 */
@Repository
public class UserAccountRepositoryImpl implements UserAccountRepository {

    private final UserAccountPersistenceRepository userAccountPersistenceRepository;
    private final RolePersistenceRepository rolePersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public UserAccountRepositoryImpl(UserAccountPersistenceRepository userAccountPersistenceRepository,
                                     RolePersistenceRepository rolePersistenceRepository,
                                     DomainEventPublisher domainEventPublisher) {
        this.userAccountPersistenceRepository = userAccountPersistenceRepository;
        this.rolePersistenceRepository = rolePersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public UserAccount save(UserAccount account) {
        var isNew = account.getId() == null;
        var roles = account.getRoles().stream()
                .map(role -> rolePersistenceRepository.findByName(role.getName())
                        .orElseThrow(() -> new IllegalArgumentException("role %s does not exist".formatted(role.getName()))))
                .collect(Collectors.toSet());
        var saved = userAccountPersistenceRepository.save(
                UserAccountPersistenceAssembler.toPersistenceFromDomain(account, roles));
        var savedAccount = UserAccountPersistenceAssembler.toDomainFromPersistence(saved);
        if (isNew) {
            savedAccount.onSignedUp();
        }
        domainEventPublisher.publishAndClearEvents(savedAccount);
        return savedAccount;
    }

    @Override
    public Optional<UserAccount> findById(Long id) {
        return userAccountPersistenceRepository.findById(id).map(UserAccountPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<UserAccount> findByEmail(EmailAddress email) {
        return userAccountPersistenceRepository.findByEmail(email.value())
                .map(UserAccountPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public boolean existsByEmail(EmailAddress email) {
        return userAccountPersistenceRepository.existsByEmail(email.value());
    }
}
