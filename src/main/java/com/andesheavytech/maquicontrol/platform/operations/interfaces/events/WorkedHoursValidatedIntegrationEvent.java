package com.andesheavytech.maquicontrol.platform.operations.interfaces.events;

import java.math.BigDecimal;

/**
 * Published when the fleet owner validates a shift of worked hours, so other contexts can react
 * to the hours a machine has accumulated.
 */
public record WorkedHoursValidatedIntegrationEvent(Long operationId, Long machineryId, BigDecimal hours) {
}
