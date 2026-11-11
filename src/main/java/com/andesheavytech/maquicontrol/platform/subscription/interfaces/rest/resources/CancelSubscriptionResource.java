package com.andesheavytech.maquicontrol.platform.subscription.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CancelSubscriptionResource(
        @NotBlank(message = "status is required")
        @Schema(example = "CANCELLED", allowableValues = {"CANCELLED"})
        String status,

        @NotBlank(message = "reason is required")
        @Size(max = 500, message = "reason cannot exceed 500 characters")
        @Schema(example = "Downsizing operations")
        String reason
) {
}
