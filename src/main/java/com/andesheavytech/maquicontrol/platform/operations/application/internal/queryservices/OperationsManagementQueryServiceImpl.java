package com.andesheavytech.maquicontrol.platform.operations.application.internal.queryservices;

import com.andesheavytech.maquicontrol.platform.operations.application.queryservices.OperationsManagementQueryService;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.aggregates.ServiceOperation;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.entities.WorkedHours;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.queries.GetOperationsByMachineryIdQuery;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.queries.GetOperationsByOperatorProfileIdQuery;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.queries.ViewServiceOperationByRentalIdQuery;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.queries.ViewServiceOperationQuery;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.queries.ViewWorkedHoursQuery;
import com.andesheavytech.maquicontrol.platform.operations.domain.repositories.ServiceOperationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link OperationsManagementQueryService}.
 */
@Service
public class OperationsManagementQueryServiceImpl implements OperationsManagementQueryService {

    private final ServiceOperationRepository serviceOperationRepository;

    public OperationsManagementQueryServiceImpl(ServiceOperationRepository serviceOperationRepository) {
        this.serviceOperationRepository = serviceOperationRepository;
    }

    @Override
    public Optional<ServiceOperation> handle(ViewServiceOperationQuery query) {
        return serviceOperationRepository.findById(query.operationId());
    }

    @Override
    public Optional<ServiceOperation> handle(ViewServiceOperationByRentalIdQuery query) {
        return serviceOperationRepository.findByRentalId(query.rentalId());
    }

    @Override
    public List<ServiceOperation> handle(GetOperationsByMachineryIdQuery query) {
        return serviceOperationRepository.findByMachineryId(query.machineryId());
    }

    @Override
    public List<ServiceOperation> handle(GetOperationsByOperatorProfileIdQuery query) {
        return serviceOperationRepository.findByOperatorProfileId(query.operatorProfileId());
    }

    @Override
    public List<WorkedHours> handle(ViewWorkedHoursQuery query) {
        return serviceOperationRepository.findById(query.operationId())
                .map(ServiceOperation::getWorkedHours)
                .orElse(List.of());
    }
}
