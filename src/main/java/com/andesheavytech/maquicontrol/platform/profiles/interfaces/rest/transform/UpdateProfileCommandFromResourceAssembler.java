package com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.profiles.domain.model.commands.UpdateProfileCommand;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.DocumentType;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.resources.ContactInformationResource;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.resources.UpdateProfileResource;

public final class UpdateProfileCommandFromResourceAssembler {

    private UpdateProfileCommandFromResourceAssembler() {
    }

    public static UpdateProfileCommand toCommandFromResource(Long profileId, Long userAccountId, UpdateProfileResource resource) {
        var contact = resource.contactInformation() == null
                ? new ContactInformationResource(null, null, null, null, null)
                : resource.contactInformation();
        return new UpdateProfileCommand(profileId, userAccountId, resource.firstName(), resource.lastName(),
                EnumFromStringAssembler.toEnum(DocumentType.class, resource.documentType(), "documentType"),
                resource.documentNumber(), contact.phoneNumber(), contact.secondaryEmail(), contact.address(),
                contact.district(), contact.city());
    }
}
