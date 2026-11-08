package com.andesheavytech.maquicontrol.platform.maintenance.interfaces.rest.resources;

import java.time.LocalDateTime;

public record BreakdownReportResource(Long id, String description, String severity, LocalDateTime reportedAt,
                                      LocalDateTime resolvedAt, boolean resolved) {
}
