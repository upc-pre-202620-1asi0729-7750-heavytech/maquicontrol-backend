package com.andesheavytech.maquicontrol.iam.infrastructure.persistence.jpa.repositories;

import com.andesheavytech.maquicontrol.iam.domain.UserAccount;
import com.andesheavytech.maquicontrol.iam.domain.repositories.UserAccountRepository;
import com.andesheavytech.maquicontrol.iam.infrastructure.persistence.jpa.assemblers.UserAccountPersistenceAssembler;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

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
    public Optional<UserAccount> findByEmail(String email) {
        return userAccountJpaRepository.findByEmail(email)
                .flatMap(accountEntity ->
                        credentialJpaRepository
                                .findByUserAccount_AccountId(accountEntity.getAccountId())
                                .map(credentialEntity ->
                                        assembler.toDomain(accountEntity, credentialEntity)
                                )
                );
    }

    @Override
    public Optional<UserAccount> findById(UUID id) {
        return userAccountJpaRepository.findByAccountId(id)
                .flatMap(accountEntity ->
                        credentialJpaRepository
                                .findByUserAccount_AccountId(accountEntity.getAccountId())
                                .map(credentialEntity ->
                                        assembler.toDomain(accountEntity, credentialEntity)
                                )
                );
    }

    @Override
    @Transactional
    public UserAccount save(UserAccount account) {
        var accountToSave = assembler.toPersistenceEntity(account);

        var savedAccount = userAccountJpaRepository.findByAccountId(account.getId())
                .map(existingAccount -> {
                    existingAccount.updateFrom(accountToSave);
                    return userAccountJpaRepository.save(existingAccount);
                })
                .orElseGet(() -> userAccountJpaRepository.save(accountToSave));

        var credentialToSave =
                assembler.toCredentialPersistenceEntity(savedAccount, account);

        credentialJpaRepository.findByUserAccount_AccountId(account.getId())
                .map(existingCredential -> {
                    existingCredential.updateFrom(credentialToSave);
                    return credentialJpaRepository.save(existingCredential);
                })
                .orElseGet(() -> credentialJpaRepository.save(credentialToSave));

        domainEventPublisher.publishAndClearEvents(account);
        return account;
    }
}