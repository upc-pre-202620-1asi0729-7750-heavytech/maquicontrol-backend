package com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record CreateProfileResource(
        @NotBlank(message = "{validation.not-blank}") String firstName,
        @NotBlank(message = "{validation.not-blank}") String lastName,
        @NotBlank(message = "{validation.not-blank}") String documentType,
        @NotBlank(message = "{validation.not-blank}") String documentNumber,
        ContactInformationResource contactInformation) {
}
