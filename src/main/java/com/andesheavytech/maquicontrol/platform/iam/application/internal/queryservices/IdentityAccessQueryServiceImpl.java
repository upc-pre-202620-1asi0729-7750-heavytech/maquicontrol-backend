package com.andesheavytech.maquicontrol.platform.iam.application.internal.queryservices;

import com.andesheavytech.maquicontrol.platform.iam.application.queryservices.IdentityAccessQueryService;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.aggregates.UserAccount;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.entities.Role;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.queries.GetSignedInAccountQuery;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.queries.GetUserAccountByIdQuery;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.queries.ViewRolesQuery;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.EmailAddress;
import com.andesheavytech.maquicontrol.platform.iam.domain.repositories.RoleRepository;
import com.andesheavytech.maquicontrol.platform.iam.domain.repositories.UserAccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link IdentityAccessQueryService}.
 */
@Service
public class IdentityAccessQueryServiceImpl implements IdentityAccessQueryService {

    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;

    public IdentityAccessQueryServiceImpl(UserAccountRepository userAccountRepository, RoleRepository roleRepository) {
        this.userAccountRepository = userAccountRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Optional<UserAccount> handle(GetSignedInAccountQuery query) {
        try {
            return userAccountRepository.findByEmail(new EmailAddress(query.email()));
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<UserAccount> handle(GetUserAccountByIdQuery query) {
        return userAccountRepository.findById(query.userAccountId());
    }

    @Override
    public List<Role> handle(ViewRolesQuery query) {
        return roleRepository.findAll();
    }
}
