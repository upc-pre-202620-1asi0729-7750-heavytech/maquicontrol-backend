package com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record UpdateCategoryResource(@NotBlank String name) {
}
