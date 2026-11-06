package com.andesheavytech.maquicontrol.iam.interfaces.rest;

import com.andesheavytech.maquicontrol.iam.application.commandservices.RegisterUserCommandService;
import com.andesheavytech.maquicontrol.iam.domain.UserAccount;
import com.andesheavytech.maquicontrol.iam.domain.RoleName;
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

    public AuthenticationController(
            RegisterUserCommandService registerUserCommandService
    ) {
        this.registerUserCommandService = registerUserCommandService;
    }

    @PostMapping("/sign-up")
    public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest request) {
        return ResponseEntityAssembler.toResponseEntityFromResult(
                registerUserCommandService.registerUser(
                        request.email(),
                        request.password()
                ),
                AuthenticationController::toResponse,
                HttpStatus.CREATED
        );
    }

    private static SignUpResponse toResponse(UserAccount account) {
        return new SignUpResponse(
                account.getId(),
                account.getEmail(),
                account.getStatus().name(),
                account.getRoles()
        );
    }

    public record SignUpRequest(
            @NotBlank @Email String email,
            @NotBlank @Size(min = 8) String password
    ) {
    }

    public record SignUpResponse(
            UUID id,
            String email,
            String status,
            Set<RoleName> roles
    ) {
    }
}