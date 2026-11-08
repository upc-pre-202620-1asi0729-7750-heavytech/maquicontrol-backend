package com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.profiles.domain.model.aggregates.Profile;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.resources.ContactInformationResource;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.resources.ProfileResource;

public final class ProfileResourceFromEntityAssembler {

    private ProfileResourceFromEntityAssembler() {
    }

    public static ProfileResource toResourceFromEntity(Profile profile) {
        var contact = profile.getContactInformation();
        return new ProfileResource(profile.getId(), profile.getUserAccountId(),
                profile.getName().firstName(), profile.getName().lastName(), profile.getFullName(),
                profile.getDocumentNumber().type().name(), profile.getDocumentNumber().value(),
                new ContactInformationResource(contact.phoneNumber(), contact.secondaryEmail(), contact.address(),
                        contact.district(), contact.city()),
                OrganizationResourceFromEntityAssembler.toResourceFromEntity(profile.getOrganization()));
    }
}
