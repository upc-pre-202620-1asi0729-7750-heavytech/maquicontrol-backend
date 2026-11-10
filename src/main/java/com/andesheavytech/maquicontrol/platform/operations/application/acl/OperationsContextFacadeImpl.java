package com.andesheavytech.maquicontrol.platform.operations.application.acl;

import com.andesheavytech.maquicontrol.platform.operations.application.queryservices.OperationsManagementQueryService;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.aggregates.ServiceOperation;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.queries.ViewServiceOperationByRentalIdQuery;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.OperationStatus;
import com.andesheavytech.maquicontrol.platform.operations.interfaces.acl.OperationsContextFacade;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Implementation of {@link OperationsContextFacade}.
 */
@Service
public class OperationsContextFacadeImpl implements OperationsContextFacade {

    private final OperationsManagementQueryService operationsManagementQueryService;

    public OperationsContextFacadeImpl(OperationsManagementQueryService operationsManagementQueryService) {
        this.operationsManagementQueryService = operationsManagementQueryService;
    }

    @Override
    public boolean existsCompletedOperation(Long rentalId) {
        if (rentalId == null || rentalId <= 0) {
            return false;
        }
        return operationsManagementQueryService.handle(new ViewServiceOperationByRentalIdQuery(rentalId))
                .filter(op -> op.getStatus() == OperationStatus.COMPLETED)
                .isPresent();
    }

    @Override
    public BigDecimal fetchTotalValidatedHours(Long rentalId) {
        if (rentalId == null || rentalId <= 0) {
            return BigDecimal.ZERO;
        }
        return operationsManagementQueryService.handle(new ViewServiceOperationByRentalIdQuery(rentalId))
                .map(ServiceOperation::calculateTotalHours)
                .map(h -> h.value())
                .orElse(BigDecimal.ZERO);
    }
}
