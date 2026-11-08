package com.andesheavytech.maquicontrol.platform.operations.interfaces.acl;

import java.math.BigDecimal;

/**
 * Facade that other bounded contexts use to read Operations data.
 */
public interface OperationsContextFacade {

    boolean existsCompletedOperation(Long rentalId);

    BigDecimal fetchTotalValidatedHours(Long rentalId);
}
