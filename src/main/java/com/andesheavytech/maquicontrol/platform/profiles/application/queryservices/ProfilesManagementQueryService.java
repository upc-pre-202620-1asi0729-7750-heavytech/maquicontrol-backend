package com.andesheavytech.maquicontrol.platform.profiles.application.queryservices;

import com.andesheavytech.maquicontrol.platform.profiles.domain.model.aggregates.Profile;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.entities.Organization;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.queries.GetOrganizationByIdQuery;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.queries.GetProfileByIdQuery;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.queries.GetProfileByUserAccountIdQuery;

import java.util.Optional;

/**
 * Query service for the Profiles bounded context.
 */
public interface ProfilesManagementQueryService {

    Optional<Profile> handle(GetProfileByIdQuery query);

    Optional<Profile> handle(GetProfileByUserAccountIdQuery query);

    Optional<Organization> handle(GetOrganizationByIdQuery query);
}
