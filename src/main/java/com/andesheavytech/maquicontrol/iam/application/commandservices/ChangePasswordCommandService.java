package com.andesheavytech.maquicontrol.iam.application.commandservices;

import com.andesheavytech.maquicontrol.iam.domain.UserAccount;
import com.andesheavytech.maquicontrol.iam.domain.repositories.UserAccountRepository;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class ChangePasswordCommandService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordCommandService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Result<UserAccount, ApplicationError> changePassword(
            UUID accountId,
            String currentPassword,
            String newPassword
    ) {
        if (accountId == null) {
            return Result.failure(
                    ApplicationError.validationError("accountId", "Es obligatorio")
            );
        }
        if (currentPassword == null || currentPassword.isBlank()) {
            return Result.failure(
                    ApplicationError.validationError("currentPassword", "Es obligatorio")
            );
        }
        if (newPassword == null
                || newPassword.length() < 8
                || newPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            return Result.failure(
                    ApplicationError.validationError(
                            "newPassword",
                            "Debe tener al menos 8 caracteres y no superar 72 bytes"
                    )
            );
        }

        var accountOptional = userAccountRepository.findById(accountId);
        if (accountOptional.isEmpty()) {
            return Result.failure(
                    ApplicationError.notFound("user_account", accountId.toString())
            );
        }

        UserAccount account = accountOptional.get();
        String currentHash = account.getCredential().getPasswordHash();

        if (!passwordEncoder.matches(currentPassword, currentHash)) {
            return Result.failure(
                    ApplicationError.businessRuleViolation(
                            "change password",
                            "La contraseña actual es incorrecta"
                    )
            );
        }

        if (passwordEncoder.matches(newPassword, currentHash)) {
            return Result.failure(
                    ApplicationError.validationError(
                            "newPassword",
                            "Debe ser diferente de la contraseña actual"
                    )
            );
        }

        account.changePassword(passwordEncoder.encode(newPassword));
        return Result.success(userAccountRepository.save(account));
    }
}