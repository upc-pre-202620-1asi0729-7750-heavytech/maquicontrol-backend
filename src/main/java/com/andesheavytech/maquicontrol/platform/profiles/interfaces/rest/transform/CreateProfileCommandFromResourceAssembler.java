package com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.DocumentType;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.resources.ContactInformationResource;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.resources.CreateProfileResource;

public final class CreateProfileCommandFromResourceAssembler {

    private CreateProfileCommandFromResourceAssembler() {
    }

    public static CreateProfileCommand toCommandFromResource(Long userAccountId, CreateProfileResource resource) {
        var contact = resource.contactInformation() == null
                ? new ContactInformationResource(null, null, null, null, null)
                : resource.contactInformation();
        return new CreateProfileCommand(userAccountId, resource.firstName(), resource.lastName(),
                EnumFromStringAssembler.toEnum(DocumentType.class, resource.documentType(), "documentType"),
                resource.documentNumber(), contact.phoneNumber(), contact.secondaryEmail(), contact.address(),
                contact.district(), contact.city());
    }
}
