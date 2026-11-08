package com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest.resources;

public record MachineryResource(Long id, Long ownerProfileId, Long categoryId, String name, String description,
                                String brand, String model, Integer manufactureYear, MoneyResource hourlyRate,
                                MachineryLocationResource location, String status) {
}
