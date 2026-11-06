package com.andesheavytech.maquicontrol.iam.infrastructure.persistence.jpa.repositories;

import com.andesheavytech.maquicontrol.iam.domain.UserAccount;
import com.andesheavytech.maquicontrol.iam.domain.repositories.UserAccountRepository;
import com.andesheavytech.maquicontrol.iam.infrastructure.persistence.jpa.assemblers.UserAccountPersistenceAssembler;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserAccountRepositoryImpl implements UserAccountRepository {

    private final UserAccountJpaRepository userAccountJpaRepository;
    private final CredentialJpaRepository credentialJpaRepository;
    private final UserAccountPersistenceAssembler assembler;
    private final DomainEventPublisher domainEventPublisher;

    public UserAccountRepositoryImpl(
            UserAccountJpaRepository userAccountJpaRepository,
            CredentialJpaRepository credentialJpaRepository,
            UserAccountPersistenceAssembler assembler,
            DomainEventPublisher domainEventPublisher
    ) {
        this.userAccountJpaRepository = userAccountJpaRepository;
        this.credentialJpaRepository = credentialJpaRepository;
        this.assembler = assembler;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userAccountJpaRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    public UserAccount save(UserAccount account) {
        var accountEntity = assembler.toPersistenceEntity(account);
        var savedAccount = userAccountJpaRepository.save(accountEntity);

        var credentialEntity =
                assembler.toCredentialPersistenceEntity(savedAccount, account);
        credentialJpaRepository.save(credentialEntity);

        domainEventPublisher.publishAndClearEvents(account);
        return account;
    }
}