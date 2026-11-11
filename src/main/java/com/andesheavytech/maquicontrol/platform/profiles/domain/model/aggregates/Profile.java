package com.andesheavytech.maquicontrol.platform.profiles.domain.model.aggregates;

import com.andesheavytech.maquicontrol.platform.profiles.domain.model.entities.Organization;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.events.ProfileCreatedEvent;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.ContactInformation;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.DocumentNumber;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.PersonName;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;

import java.util.Objects;
import java.util.Optional;

/**
 * Profile aggregate root: personal data of a user account and, optionally, its organization.
 */
@Getter
public class Profile extends AbstractDomainAggregateRoot<Profile> {

    private final Long id;
    private final Long userAccountId;
    private PersonName name;
    private DocumentNumber documentNumber;
    private ContactInformation contactInformation;
    private Organization organization;

    private Profile(Long id, Long userAccountId, PersonName name, DocumentNumber documentNumber,
                    ContactInformation contactInformation, Organization organization) {
        if (userAccountId == null || userAccountId <= 0) {
            throw new IllegalArgumentException("userAccountId is required");
        }
        this.id = id;
        this.userAccountId = userAccountId;
        this.name = Objects.requireNonNull(name, "name is required");
        this.documentNumber = Objects.requireNonNull(documentNumber, "documentNumber is required");
        this.contactInformation = contactInformation == null ? ContactInformation.empty() : contactInformation;
        this.organization = organization;
    }

    public static Profile create(Long userAccountId, PersonName name, DocumentNumber documentNumber,
                                 ContactInformation contactInformation) {
        return new Profile(null, userAccountId, name, documentNumber, contactInformation, null);
    }

    public static Profile reconstitute(Long id, Long userAccountId, PersonName name, DocumentNumber documentNumber,
                                       ContactInformation contactInformation, Organization organization) {
        return new Profile(id, userAccountId, name, documentNumber, contactInformation, organization);
    }

    /**
     * Registers the {@link ProfileCreatedEvent}. Called by the repository adapter once the id is assigned.
     */
    public void onCreated() {
        registerDomainEvent(new ProfileCreatedEvent(id, userAccountId));
    }

    public void update(PersonName name, DocumentNumber documentNumber, ContactInformation contactInformation) {
        this.name = Objects.requireNonNull(name, "name is required");
        this.documentNumber = Objects.requireNonNull(documentNumber, "documentNumber is required");
        this.contactInformation = contactInformation == null ? ContactInformation.empty() : contactInformation;
    }

    /**
     * Links an organization to this profile. A profile belongs to one organization at most.
     *
     * @throws IllegalStateException when the profile already has an organization
     */
    public void registerOrganization(Organization organization) {
        if (this.organization != null) {
            throw new IllegalStateException("profile already belongs to an organization");
        }
        this.organization = Objects.requireNonNull(organization, "organization is required");
    }

    public boolean belongsTo(Long userAccountId) {
        return this.userAccountId.equals(userAccountId);
    }

    public Optional<Organization> organization() {
        return Optional.ofNullable(organization);
    }

    public String getFullName() {
        return name.fullName();
    }
}
