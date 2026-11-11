package com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands;

import java.math.BigDecimal;

/**
 * Completes a maintenance. The cost is optional: both amount and currency, or neither.
 */
public record CompleteMaintenanceCommand(Long maintenanceId, Long ownerProfileId, BigDecimal costAmount,
                                         String costCurrency) {
}
