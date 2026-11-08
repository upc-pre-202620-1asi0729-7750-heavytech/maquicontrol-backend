package com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence.jpa.adapters;

import com.andesheavytech.maquicontrol.platform.profiles.domain.model.aggregates.Profile;
import com.andesheavytech.maquicontrol.platform.profiles.domain.repositories.ProfileRepository;
import com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence.jpa.assemblers.ProfilePersistenceAssembler;
import com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence.jpa.repositories.OrganizationPersistenceRepository;
import com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence.jpa.repositories.ProfilePersistenceRepository;
import com.andesheavytech.maquicontrol.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Adapter that implements {@link ProfileRepository} with Spring Data JPA.
 */
@Repository
public class ProfileRepositoryImpl implements ProfileRepository {

    private final ProfilePersistenceRepository profilePersistenceRepository;
    private final OrganizationPersistenceRepository organizationPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public ProfileRepositoryImpl(ProfilePersistenceRepository profilePersistenceRepository,
                                 OrganizationPersistenceRepository organizationPersistenceRepository,
                                 DomainEventPublisher domainEventPublisher) {
        this.profilePersistenceRepository = profilePersistenceRepository;
        this.organizationPersistenceRepository = organizationPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Profile save(Profile profile) {
        var isNew = profile.getId() == null;
        var saved = profilePersistenceRepository.save(ProfilePersistenceAssembler.toPersistenceFromDomain(profile));
        var savedProfile = ProfilePersistenceAssembler.toDomainFromPersistence(saved);
        if (isNew) {
            savedProfile.onCreated();
        }
        domainEventPublisher.publishAndClearEvents(savedProfile);
        return savedProfile;
    }

    @Override
    public Optional<Profile> findById(Long id) {
        return profilePersistenceRepository.findById(id).map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Profile> findByUserAccountId(Long userAccountId) {
        return profilePersistenceRepository.findByUserAccountId(userAccountId)
                .map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Profile> findByOrganizationId(Long organizationId) {
        return profilePersistenceRepository.findFirstByOrganizationId(organizationId)
                .map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public boolean existsByUserAccountId(Long userAccountId) {
        return profilePersistenceRepository.existsByUserAccountId(userAccountId);
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber, Long excludedProfileId) {
        return excludedProfileId == null
                ? profilePersistenceRepository.existsByDocumentNumber(documentNumber)
                : profilePersistenceRepository.existsByDocumentNumberAndIdNot(documentNumber, excludedProfileId);
    }

    @Override
    public boolean existsOrganizationByTaxId(String taxId, Long excludedOrganizationId) {
        return excludedOrganizationId == null
                ? organizationPersistenceRepository.existsByTaxId(taxId)
                : organizationPersistenceRepository.existsByTaxIdAndIdNot(taxId, excludedOrganizationId);
    }
}
