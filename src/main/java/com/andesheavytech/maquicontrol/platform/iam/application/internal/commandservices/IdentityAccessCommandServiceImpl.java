package com.andesheavytech.maquicontrol.platform.iam.application.internal.commandservices;

import com.andesheavytech.maquicontrol.platform.iam.application.commandservices.AuthenticatedUserAccount;
import com.andesheavytech.maquicontrol.platform.iam.application.commandservices.IdentityAccessCommandService;
import com.andesheavytech.maquicontrol.platform.iam.application.internal.outboundservices.hashing.HashingService;
import com.andesheavytech.maquicontrol.platform.iam.application.internal.outboundservices.tokens.TokenService;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.aggregates.UserAccount;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.commands.SeedRolesCommand;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.commands.SignInCommand;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.commands.SignUpCommand;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.entities.Role;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.Credential;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.EmailAddress;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.PlainPassword;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.RoleName;
import com.andesheavytech.maquicontrol.platform.iam.domain.repositories.RoleRepository;
import com.andesheavytech.maquicontrol.platform.iam.domain.repositories.UserAccountRepository;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * Implementation of {@link IdentityAccessCommandService}.
 */
@Service
public class IdentityAccessCommandServiceImpl implements IdentityAccessCommandService {

    private static final String INVALID_CREDENTIALS = "email or password is incorrect";

    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;

    public IdentityAccessCommandServiceImpl(UserAccountRepository userAccountRepository,
                                            RoleRepository roleRepository,
                                            HashingService hashingService,
                                            TokenService tokenService) {
        this.userAccountRepository = userAccountRepository;
        this.roleRepository = roleRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
    }

    @Override
    @Transactional
    public Result<UserAccount, ApplicationError> handle(SignUpCommand command) {
        EmailAddress email;
        PlainPassword password;
        try {
            email = new EmailAddress(command.email());
            password = new PlainPassword(command.password());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.validationError("sign up", e.getMessage()));
        }
        if (command.roleName() == null) {
            return Result.failure(ApplicationError.validationError("roleName", "role is required"));
        }
        if (!command.roleName().isSelfAssignable()) {
            return Result.failure(ApplicationError.businessRuleViolation(
                    "self assignable role", "role %s cannot be chosen on sign up".formatted(command.roleName())));
        }
        if (userAccountRepository.existsByEmail(email)) {
            return Result.failure(ApplicationError.conflict("user_account", "email %s is already registered".formatted(email.value())));
        }
        var role = roleRepository.findByName(command.roleName());
        if (role.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Role", command.roleName().name()));
        }
        var credential = Credential.of(hashingService.encode(password.value()));
        var account = UserAccount.signUp(email, credential, role.get());
        return Result.success(userAccountRepository.save(account));
    }

    @Override
    @Transactional
    public Result<AuthenticatedUserAccount, ApplicationError> handle(SignInCommand command) {
        EmailAddress email;
        try {
            email = new EmailAddress(command.email());
        } catch (IllegalArgumentException e) {
            return Result.failure(ApplicationError.unauthorized(INVALID_CREDENTIALS));
        }
        var found = userAccountRepository.findByEmail(email);
        if (found.isEmpty() || command.password() == null
                || !hashingService.matches(command.password(), found.get().getCredential().passwordHash())) {
            return Result.failure(ApplicationError.unauthorized(INVALID_CREDENTIALS));
        }
        var account = found.get();
        if (!account.isActive()) {
            return Result.failure(ApplicationError.forbidden("user_account", "account is %s".formatted(account.getStatus())));
        }
        account.signIn();
        var saved = userAccountRepository.save(account);
        var token = tokenService.generateToken(saved.getEmailValue());
        return Result.success(new AuthenticatedUserAccount(saved, token));
    }

    @Override
    @Transactional
    public void handle(SeedRolesCommand command) {
        Arrays.stream(RoleName.values())
                .filter(roleName -> !roleRepository.existsByName(roleName))
                .forEach(roleName -> roleRepository.save(new Role(roleName)));
    }
}
