package com.andesheavytech.maquicontrol.iam.application.commandservices;

import com.andesheavytech.maquicontrol.iam.domain.UserAccount;
import com.andesheavytech.maquicontrol.iam.domain.repositories.UserAccountRepository;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.UUID;

@Service
public class RegisterUserCommandService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserCommandService(
            UserAccountRepository userAccountRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Result<UserAccount, ApplicationError> registerUser(
            String email,
            String password
    ) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            return Result.failure(
                    ApplicationError.validationError("email", "Debe ser un correo válido")
            );
        }

        if (password == null || password.length() < 8
                || password.getBytes(StandardCharsets.UTF_8).length > 72) {
            return Result.failure(
                    ApplicationError.validationError(
                            "password",
                            "La contraseña debe tener al menos 8 caracteres y no superar 72 bytes"
                    )
            );
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        if (userAccountRepository.existsByEmail(normalizedEmail)) {
            return Result.failure(
                    ApplicationError.conflict("user account", "El correo ya está registrado")
            );
        }

        try {
            String passwordHash = passwordEncoder.encode(password);
            UserAccount account = new UserAccount(
                    UUID.randomUUID(),
                    normalizedEmail,
                    passwordHash
            );
            return Result.success(userAccountRepository.save(account));
        } catch (IllegalArgumentException exception) {
            return Result.failure(
                    ApplicationError.validationError("user account", exception.getMessage())
            );
        }
    }
}