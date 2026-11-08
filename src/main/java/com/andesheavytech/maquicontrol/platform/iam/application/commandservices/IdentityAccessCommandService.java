package com.andesheavytech.maquicontrol.platform.iam.application.commandservices;

import com.andesheavytech.maquicontrol.platform.iam.domain.model.aggregates.UserAccount;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.commands.SeedRolesCommand;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.commands.SignInCommand;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.commands.SignUpCommand;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;

/**
 * Command service for the Identity and Access Management bounded context.
 */
public interface IdentityAccessCommandService {

    Result<UserAccount, ApplicationError> handle(SignUpCommand command);

    Result<AuthenticatedUserAccount, ApplicationError> handle(SignInCommand command);

    void handle(SeedRolesCommand command);
}
