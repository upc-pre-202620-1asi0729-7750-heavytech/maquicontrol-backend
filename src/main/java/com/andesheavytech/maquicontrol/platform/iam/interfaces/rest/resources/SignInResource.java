package com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for {@code POST /api/v1/authentication/sign-in}.
 */
public record SignInResource(
        @NotBlank(message = "{validation.not-blank}") String email,
        @NotBlank(message = "{validation.not-blank}") String password) {
}
