package com.andesheavytech.maquicontrol.platform.operations.interfaces.acl;

import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * Facade that other bounded contexts use to read Operations data.
 */
public interface OperationsContextFacade {

    boolean existsCompletedOperation(Long rentalId);

    BigDecimal fetchTotalValidatedHours(Long rentalId);

    /**
     * Hours validated on a machine after the given moment, across all its operations.
     */
    BigDecimal fetchValidatedHoursOfMachinerySince(Long machineryId, LocalDateTime since);
}
