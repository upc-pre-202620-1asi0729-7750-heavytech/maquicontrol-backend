package com.andesheavytech.maquicontrol.platform.maintenance.application.queryservices;

import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.aggregates.Maintenance;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.queries.CheckMaintenanceDetailsQuery;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.queries.GetMaintenancesByOwnerProfileIdQuery;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.queries.ViewMaintenanceHistoryQuery;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.queries.ViewPendingMaintenanceQuery;

import java.util.List;
import java.util.Optional;

/**
 * Query service for the Maintenance Management bounded context.
 */
public interface MaintenanceManagementQueryService {

    Optional<Maintenance> handle(CheckMaintenanceDetailsQuery query);

    List<Maintenance> handle(ViewMaintenanceHistoryQuery query);

    List<Maintenance> handle(ViewPendingMaintenanceQuery query);

    List<Maintenance> handle(GetMaintenancesByOwnerProfileIdQuery query);
}
