package com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.iam.application.commandservices.AuthenticatedUserAccount;
import com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.resources.AuthenticatedUserAccountResource;

public final class AuthenticatedUserAccountResourceFromEntityAssembler {

    private AuthenticatedUserAccountResourceFromEntityAssembler() {
    }

    public static AuthenticatedUserAccountResource toResourceFromEntity(AuthenticatedUserAccount authenticated) {
        var account = authenticated.userAccount();
        var roles = account.getRoles().stream().map(role -> role.getName().name()).sorted().toList();
        return new AuthenticatedUserAccountResource(account.getId(), account.getEmailValue(), roles, authenticated.token());
    }
}
