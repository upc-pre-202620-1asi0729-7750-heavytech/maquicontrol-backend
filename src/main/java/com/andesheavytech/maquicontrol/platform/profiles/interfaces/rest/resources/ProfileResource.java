package com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest.resources;

public record ProfileResource(Long id, Long userAccountId, String firstName, String lastName, String fullName,
                              String documentType, String documentNumber,
                              ContactInformationResource contactInformation, OrganizationResource organization) {
}
