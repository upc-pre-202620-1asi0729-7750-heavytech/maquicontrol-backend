package com.andesheavytech.maquicontrol.platform.profiles.application.acl;

import com.andesheavytech.maquicontrol.platform.profiles.application.internal.outboundservices.acl.ExternalIamService;
import com.andesheavytech.maquicontrol.platform.profiles.application.queryservices.ProfilesManagementQueryService;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.aggregates.Profile;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.queries.GetProfileByUserAccountIdQuery;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.queries.ViewProfileQuery;
import com.andesheavytech.maquicontrol.platform.profiles.interfaces.acl.ProfilesContextFacade;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link ProfilesContextFacade}.
 */
@Service
public class ProfilesContextFacadeImpl implements ProfilesContextFacade {

    private final ProfilesManagementQueryService profilesManagementQueryService;
    private final ExternalIamService externalIamService;

    public ProfilesContextFacadeImpl(ProfilesManagementQueryService profilesManagementQueryService,
                                     ExternalIamService externalIamService) {
        this.profilesManagementQueryService = profilesManagementQueryService;
        this.externalIamService = externalIamService;
    }

    @Override
    public Long fetchProfileIdByUserEmail(String email) {
        return externalIamService.fetchUserAccountIdByEmail(email)
                .flatMap(userAccountId -> profilesManagementQueryService.handle(new GetProfileByUserAccountIdQuery(userAccountId)))
                .map(Profile::getId)
                .orElse(0L);
    }

    @Override
    public boolean existsProfile(Long profileId) {
        return profilesManagementQueryService.handle(new ViewProfileQuery(profileId)).isPresent();
    }

    @Override
    public String fetchFullName(Long profileId) {
        return profilesManagementQueryService.handle(new ViewProfileQuery(profileId))
                .map(Profile::getFullName)
                .orElse("");
    }
}
