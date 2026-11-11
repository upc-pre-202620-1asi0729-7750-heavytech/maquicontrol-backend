package com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.iam.domain.model.aggregates.UserAccount;
import com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.resources.UserAccountResource;

public final class UserAccountResourceFromEntityAssembler {

    private UserAccountResourceFromEntityAssembler() {
    }

    public static UserAccountResource toResourceFromEntity(UserAccount account) {
        var roles = account.getRoles().stream().map(role -> role.getName().name()).sorted().toList();
        return new UserAccountResource(account.getId(), account.getEmailValue(), account.getStatus().name(),
                roles, account.getLastLoginAt());
    }
}
