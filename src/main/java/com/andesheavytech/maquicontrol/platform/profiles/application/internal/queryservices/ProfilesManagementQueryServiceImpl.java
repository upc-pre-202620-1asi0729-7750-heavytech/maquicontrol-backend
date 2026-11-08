package com.andesheavytech.maquicontrol.platform.profiles.application.internal.queryservices;

import com.andesheavytech.maquicontrol.platform.profiles.application.queryservices.ProfilesManagementQueryService;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.aggregates.Profile;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.entities.Organization;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.queries.GetOrganizationByIdQuery;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.queries.GetProfileByIdQuery;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.queries.GetProfileByUserAccountIdQuery;
import com.andesheavytech.maquicontrol.platform.profiles.domain.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Implementation of {@link ProfilesManagementQueryService}.
 */
@Service
public class ProfilesManagementQueryServiceImpl implements ProfilesManagementQueryService {

    private final ProfileRepository profileRepository;

    public ProfilesManagementQueryServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> handle(GetProfileByIdQuery query) {
        return profileRepository.findById(query.profileId());
    }

    @Override
    public Optional<Profile> handle(GetProfileByUserAccountIdQuery query) {
        return profileRepository.findByUserAccountId(query.userAccountId());
    }

    @Override
    public Optional<Organization> handle(GetOrganizationByIdQuery query) {
        return profileRepository.findByOrganizationId(query.organizationId()).map(Profile::getOrganization);
    }
}
