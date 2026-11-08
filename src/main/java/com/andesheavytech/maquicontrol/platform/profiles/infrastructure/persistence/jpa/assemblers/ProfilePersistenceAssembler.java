package com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence.jpa.assemblers;

import com.andesheavytech.maquicontrol.platform.profiles.domain.model.aggregates.Profile;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.ContactInformation;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.DocumentNumber;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.PersonName;
import com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;

public final class ProfilePersistenceAssembler {

    private ProfilePersistenceAssembler() {
    }

    public static Profile toDomainFromPersistence(ProfilePersistenceEntity entity) {
        return Profile.reconstitute(
                entity.getId(),
                entity.getUserAccountId(),
                new PersonName(entity.getFirstName(), entity.getLastName()),
                new DocumentNumber(entity.getDocumentType(), entity.getDocumentNumber()),
                new ContactInformation(entity.getPhoneNumber(), entity.getSecondaryEmail(), entity.getAddress(),
                        entity.getDistrict(), entity.getCity()),
                OrganizationPersistenceAssembler.toDomainFromPersistence(entity.getOrganization()));
    }

    public static ProfilePersistenceEntity toPersistenceFromDomain(Profile profile) {
        var entity = new ProfilePersistenceEntity();
        if (profile.getId() != null) {
            entity.setId(profile.getId());
        }
        entity.setUserAccountId(profile.getUserAccountId());
        entity.setFirstName(profile.getName().firstName());
        entity.setLastName(profile.getName().lastName());
        entity.setDocumentType(profile.getDocumentNumber().type());
        entity.setDocumentNumber(profile.getDocumentNumber().value());
        var contact = profile.getContactInformation();
        entity.setPhoneNumber(contact.phoneNumber());
        entity.setSecondaryEmail(contact.secondaryEmail());
        entity.setAddress(contact.address());
        entity.setDistrict(contact.district());
        entity.setCity(contact.city());
        entity.setOrganization(OrganizationPersistenceAssembler.toPersistenceFromDomain(profile.getOrganization()));
        return entity;
    }
}
