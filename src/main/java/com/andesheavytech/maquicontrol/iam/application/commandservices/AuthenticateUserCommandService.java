package com.andesheavytech.maquicontrol.iam.application.commandservices;

import com.andesheavytech.maquicontrol.iam.domain.AccountStatus;
import com.andesheavytech.maquicontrol.iam.domain.UserAccount;
import com.andesheavytech.maquicontrol.iam.domain.repositories.UserAccountRepository;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class AuthenticateUserCommandService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthenticateUserCommandService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Result<UserAccount, ApplicationError> authenticateUser(
            String email,
            String password
    ) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            return Result.failure(
                    ApplicationError.validationError(
                            "credentials",
                            "Correo y contraseña son obligatorios"
                    )
            );
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        return userAccountRepository.findByEmail(normalizedEmail)
                .filter(account -> account.getStatus() == AccountStatus.ACTIVE)
                .filter(account ->
                        passwordEncoder.matches(
                                password,
                                account.getCredential().getPasswordHash()
                        )
                )
                .map(account -> {
                    account.recordSuccessfulLogin();
                    return Result.<UserAccount, ApplicationError>success(
                            userAccountRepository.save(account)
                    );
                })
                .orElseGet(() -> Result.failure(
                        ApplicationError.businessRuleViolation(
                                "authentication",
                                "Correo o contraseña incorrectos"
                        )
                ));
    }
}