package com.andesheavytech.maquicontrol.platform.profiles.domain.model.commands;

import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.OrganizationType;

public record UpdateOrganizationCommand(Long organizationId, Long userAccountId, String legalName, String tradeName,
                                        String taxId, OrganizationType organizationType, String address) {
}
