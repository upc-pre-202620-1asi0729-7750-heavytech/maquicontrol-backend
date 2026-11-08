package com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources;

import jakarta.validation.constraints.NotBlank;

public record MachineryLocationResource(@NotBlank String department, @NotBlank String province,
                                        @NotBlank String district, @NotBlank String address,
                                        Double latitude, Double longitude) {
}
