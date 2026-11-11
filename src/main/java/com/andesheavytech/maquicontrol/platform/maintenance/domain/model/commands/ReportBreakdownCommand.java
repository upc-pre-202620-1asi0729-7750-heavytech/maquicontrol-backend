package com.andesheavytech.maquicontrol.platform.maintenance.domain.model.commands;

import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.BreakdownSeverity;

/**
 * Reports a breakdown on a machine. Allowed for its owner or for a contractor with a rental in progress.
 */
public record ReportBreakdownCommand(Long machineryId, Long reporterProfileId, String description,
                                     BreakdownSeverity severity) {
}
