package com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.resources;

import java.math.BigDecimal;

public record MaintenanceCostResource(BigDecimal amount, String currency) {
}
