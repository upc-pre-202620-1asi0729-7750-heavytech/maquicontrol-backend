package com.andesheavytech.maquicontrol.platform.maintenance.application.internal.outboundservices.acl;

import com.andesheavytech.maquicontrol.platform.operations.interfaces.acl.OperationsContextFacade;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Outbound ACL to Operations Management.
 */
@Service("maintenanceExternalOperationsService")
public class ExternalOperationsService {

    private final OperationsContextFacade operationsContextFacade;

    public ExternalOperationsService(OperationsContextFacade operationsContextFacade) {
        this.operationsContextFacade = operationsContextFacade;
    }

    /**
     * Hours validated on the machine after the given moment ({@code null} counts every validated hour).
     */
    public BigDecimal fetchValidatedHoursSince(Long machineryId, LocalDateTime since) {
        var hours = operationsContextFacade.fetchValidatedHoursOfMachinerySince(machineryId, since);
        return hours == null ? BigDecimal.ZERO : hours;
    }
}
