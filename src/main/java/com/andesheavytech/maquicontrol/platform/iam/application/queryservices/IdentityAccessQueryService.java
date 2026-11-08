package com.andesheavytech.maquicontrol.platform.iam.application.queryservices;

import com.andesheavytech.maquicontrol.platform.iam.domain.model.aggregates.UserAccount;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.entities.Role;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.queries.GetSignedInAccountQuery;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.queries.GetUserAccountByIdQuery;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.queries.ViewRolesQuery;

import java.util.List;
import java.util.Optional;

/**
 * Query service for the Identity and Access Management bounded context.
 */
public interface IdentityAccessQueryService {

    Optional<UserAccount> handle(GetSignedInAccountQuery query);

    Optional<UserAccount> handle(GetUserAccountByIdQuery query);

    List<Role> handle(ViewRolesQuery query);
}
