package com.andesheavytech.maquicontrol.platform.iam.application.acl;

import com.andesheavytech.maquicontrol.platform.iam.application.queryservices.IdentityAccessQueryService;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.queries.GetSignedInAccountQuery;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.queries.GetUserAccountByIdQuery;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.RoleName;
import com.andesheavytech.maquicontrol.platform.iam.interfaces.acl.IamContextFacade;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Implementation of {@link IamContextFacade}.
 */
@Service
public class IamContextFacadeImpl implements IamContextFacade {

    private final IdentityAccessQueryService identityAccessQueryService;

    public IamContextFacadeImpl(IdentityAccessQueryService identityAccessQueryService) {
        this.identityAccessQueryService = identityAccessQueryService;
    }

    @Override
    public Long fetchUserAccountIdByEmail(String email) {
        return identityAccessQueryService.handle(new GetSignedInAccountQuery(email))
                .map(account -> account.getId())
                .orElse(0L);
    }

    @Override
    public boolean hasRole(Long userAccountId, String roleName) {
        var known = Arrays.stream(RoleName.values()).anyMatch(name -> name.name().equals(roleName));
        if (!known) {
            return false;
        }
        return identityAccessQueryService.handle(new GetUserAccountByIdQuery(userAccountId))
                .map(account -> account.hasRole(RoleName.valueOf(roleName)))
                .orElse(false);
    }
}
