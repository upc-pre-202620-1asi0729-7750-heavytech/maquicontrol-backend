package com.andesheavytech.maquicontrol.iam.interfaces.rest;

import com.andesheavytech.maquicontrol.iam.application.commandservices.AuthenticateUserCommandService;
import com.andesheavytech.maquicontrol.iam.application.commandservices.RegisterUserCommandService;
import com.andesheavytech.maquicontrol.iam.domain.RoleName;
import com.andesheavytech.maquicontrol.iam.domain.UserAccount;
import com.andesheavytech.maquicontrol.iam.infrastructure.security.JwtTokenService;
import com.andesheavytech.maquicontrol.platform.shared.interfaces.rest.transform.ResponseEntityAssembler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {

    private final RegisterUserCommandService registerUserCommandService;
    private final AuthenticateUserCommandService authenticateUserCommandService;
    private final JwtTokenService jwtTokenService;

    public AuthenticationController(
            RegisterUserCommandService registerUserCommandService,
            AuthenticateUserCommandService authenticateUserCommandService,
            JwtTokenService jwtTokenService
    ) {
        this.registerUserCommandService = registerUserCommandService;
        this.authenticateUserCommandService = authenticateUserCommandService;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntityAssembler.toResponseEntityFromResult(
                registerUserCommandService.registerUser(
                        request.email(),
                        request.password()
                ),
                AuthenticationController::toSignUpResponse,
                HttpStatus.CREATED
        );
    }

    @PostMapping("/sign-in")
    public ResponseEntity<?> signIn(@Valid @RequestBody SignInRequest request) {
        return ResponseEntityAssembler.toResponseEntityFromResult(
                authenticateUserCommandService.authenticateUser(
                        request.email(),
                        request.password()
                ),
                this::toSignInResponse,
                HttpStatus.OK
        );
    }

    private static SignUpResponse toSignUpResponse(UserAccount account) {
        return new SignUpResponse(
                account.getId(),
                account.getEmail(),
                account.getStatus().name(),
                account.getRoles()
        );
    }

    private SignInResponse toSignInResponse(UserAccount account) {
        return new SignInResponse(
                jwtTokenService.generateToken(account),
                "Bearer",
                account.getId(),
                account.getEmail(),
                account.getRoles()
        );
    }

    public record SignUpRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8) String password
    ) {
    }

    public record SignInRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {
    }

    public record SignUpResponse(
            UUID id,
            String email,
            String status,
            Set<RoleName> roles
    ) {
    }

    public record SignInResponse(
            String accessToken,
            String tokenType,
            UUID id,
            String email,
            Set<RoleName> roles
    ) {
    }
}