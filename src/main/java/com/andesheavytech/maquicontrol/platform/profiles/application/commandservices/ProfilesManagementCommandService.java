package com.andesheavytech.maquicontrol.platform.profiles.application.commandservices;

import com.andesheavytech.maquicontrol.platform.profiles.domain.model.aggregates.Profile;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.commands.CreateOrganizationCommand;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.commands.UpdateOrganizationCommand;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.commands.UpdateProfileCommand;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;

/**
 * Command service for the Profiles bounded context.
 */
public interface ProfilesManagementCommandService {

    Result<Profile, ApplicationError> handle(CreateProfileCommand command);

    Result<Profile, ApplicationError> handle(UpdateProfileCommand command);

    Result<Profile, ApplicationError> handle(CreateOrganizationCommand command);

    Result<Profile, ApplicationError> handle(UpdateOrganizationCommand command);
}
