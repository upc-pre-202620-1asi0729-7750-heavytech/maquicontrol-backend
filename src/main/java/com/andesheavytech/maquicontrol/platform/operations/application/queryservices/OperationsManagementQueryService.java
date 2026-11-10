package com.andesheavytech.maquicontrol.platform.operations.application.queryservices;

import com.andesheavytech.maquicontrol.platform.operations.domain.model.aggregates.ServiceOperation;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.entities.WorkedHours;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.queries.GetOperationsByMachineryIdQuery;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.queries.GetOperationsByOperatorProfileIdQuery;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.queries.ViewServiceOperationByRentalIdQuery;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.queries.ViewServiceOperationQuery;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.queries.ViewWorkedHoursQuery;

import java.util.List;
import java.util.Optional;

/**
 * Query service port for Operations Management.
 */
public interface OperationsManagementQueryService {

    Optional<ServiceOperation> handle(ViewServiceOperationQuery query);

    Optional<ServiceOperation> handle(ViewServiceOperationByRentalIdQuery query);

    List<ServiceOperation> handle(GetOperationsByMachineryIdQuery query);

    List<ServiceOperation> handle(GetOperationsByOperatorProfileIdQuery query);

    List<WorkedHours> handle(ViewWorkedHoursQuery query);
}
