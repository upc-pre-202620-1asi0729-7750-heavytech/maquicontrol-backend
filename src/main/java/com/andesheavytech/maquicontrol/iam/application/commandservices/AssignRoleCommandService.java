package com.andesheavytech.maquicontrol.iam.application.commandservices;

import com.andesheavytech.maquicontrol.iam.domain.RoleName;
import com.andesheavytech.maquicontrol.iam.domain.UserAccount;
import com.andesheavytech.maquicontrol.iam.domain.repositories.UserAccountRepository;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AssignRoleCommandService {

    private final UserAccountRepository userAccountRepository;

    public AssignRoleCommandService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    public Result<UserAccount, ApplicationError> assignRole(
            UUID accountId,
            RoleName role
    ) {
        if (accountId == null) {
            return Result.failure(
                    ApplicationError.validationError("accountId", "Es obligatorio")
            );
        }
        if (role == null) {
            return Result.failure(
                    ApplicationError.validationError("role", "Es obligatorio")
            );
        }

        var accountOptional = userAccountRepository.findById(accountId);
        if (accountOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound("user_account", accountId.toString())
            );
        }

        UserAccount account = accountOptional.get();
        account.assignRole(role);

        return Result.success(userAccountRepository.save(account));
    }
}