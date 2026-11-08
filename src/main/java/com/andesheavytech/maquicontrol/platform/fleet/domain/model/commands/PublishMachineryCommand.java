package com.andesheavytech.maquicontrol.platform.fleet.domain.model.commands;

import java.math.BigDecimal;

public record PublishMachineryCommand(Long ownerProfileId, Long categoryId, String name, String description,
                                      String brand, String model, Integer manufactureYear,
                                      BigDecimal hourlyRateAmount, String hourlyRateCurrency,
                                      String department, String province, String district, String address,
                                      Double latitude, Double longitude) {
}
