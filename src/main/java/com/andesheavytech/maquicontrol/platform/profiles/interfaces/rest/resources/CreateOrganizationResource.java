package com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record CreateOrganizationResource(
        @NotBlank(message = "{validation.not-blank}") String legalName,
        String tradeName,
        @NotBlank(message = "{validation.not-blank}") String taxId,
        @NotBlank(message = "{validation.not-blank}") String type,
        String address) {
}
