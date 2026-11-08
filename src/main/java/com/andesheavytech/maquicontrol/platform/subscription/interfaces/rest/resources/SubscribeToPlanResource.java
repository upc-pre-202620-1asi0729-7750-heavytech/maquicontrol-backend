package com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record SubscribeToPlanResource(
        @NotBlank(message = "planCode is required")
        @Schema(example = "ESSENTIAL", allowableValues = {"ESSENTIAL", "PRO"})
        String planCode
) {
}
