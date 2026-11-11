package com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for {@code POST /api/v1/authentication/sign-up}.
 */
public record SignUpResource(
        @NotBlank(message = "{validation.not-blank}") @Email(message = "{validation.email}") String email,
        @NotBlank(message = "{validation.not-blank}") @Size(min = 8, max = 72) String password,
        @NotBlank(message = "{validation.not-blank}") String roleName) {
}
