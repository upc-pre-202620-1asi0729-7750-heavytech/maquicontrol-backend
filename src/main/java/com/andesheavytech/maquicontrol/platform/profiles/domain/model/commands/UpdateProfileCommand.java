package com.andesheavytech.maquicontrol.platform.profiles.domain.model.commands;

import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.DocumentType;

public record UpdateProfileCommand(Long profileId, Long userAccountId, String firstName, String lastName,
                                   DocumentType documentType, String documentNumber,
                                   String phoneNumber, String secondaryEmail, String address,
                                   String district, String city) {
}
