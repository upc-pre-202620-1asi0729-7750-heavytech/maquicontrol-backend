package com.andesheavytech.maquicontrol.iam.application.commandservices;

import com.andesheavytech.maquicontrol.iam.domain.UserAccount;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;

import java.util.UUID;

public class RegisterUserCommandService {

    public Result<UserAccount, ApplicationError> registerUser(
            String email,
            String passwordHash
    ) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            return Result.failure(
                    ApplicationError.validationError("email", "Debe ser un correo válido")
            );
        }

        try {
            UserAccount account = new UserAccount(
                    UUID.randomUUID(),
                    email,
                    passwordHash
            );
            return Result.success(account);
        } catch (IllegalArgumentException exception) {
            return Result.failure(
                    ApplicationError.validationError("user account", exception.getMessage())
            );
        }
    }
}