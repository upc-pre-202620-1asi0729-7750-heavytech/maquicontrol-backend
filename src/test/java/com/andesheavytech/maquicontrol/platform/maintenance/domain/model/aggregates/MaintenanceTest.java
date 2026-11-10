package com.andesheavytech.maquicontrol.platform.maintenance.domain.model.aggregates;

import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.entities.BreakdownReport;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.BreakdownSeverity;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.MaintenanceStatus;
import com.andesheavytech.maquicontrol.platform.maintenance.domain.model.valueobjects.MaintenanceType;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MaintenanceTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 11, 9, 10, 0);

    private Maintenance scheduledMaintenance() {
        var maintenance = Maintenance.schedule(1L, MaintenanceType.PREVENTIVE, "Oil change", NOW.plusDays(2),
                "Juan Perez", NOW);
        return Maintenance.reconstitute(5L, maintenance.getMachineryId(), maintenance.getType(), maintenance.getStatus(),
                maintenance.getDescription(), maintenance.getScheduledDate(), null, null,
                maintenance.getTechnicianName(), null, maintenance.getBreakdownReports());
    }

    @Test
    void scheduleRejectsPastDate() {
        assertThrows(IllegalArgumentException.class, () -> Maintenance.schedule(1L, MaintenanceType.INSPECTION,
                "Inspection", NOW.minusDays(1), null, NOW));
    }

    @Test
    void startAndCompleteRecordsCostAndResolvesBreakdowns() {
        var maintenance = scheduledMaintenance();
        maintenance.reportBreakdown(BreakdownReport.report("Hydraulic leak", BreakdownSeverity.HIGH));

        maintenance.start();
        maintenance.complete(new Money(new BigDecimal("350.00"), "PEN"));

        assertEquals(MaintenanceStatus.COMPLETED, maintenance.getStatus());
        assertEquals(new BigDecimal("350.00"), maintenance.getCost().amount());
        assertTrue(maintenance.getBreakdownReports().stream().allMatch(BreakdownReport::isResolved));
    }

    @Test
    void completeRequiresMaintenanceInProgress() {
        assertThrows(IllegalStateException.class, () -> scheduledMaintenance().complete(null));
    }

    @Test
    void cancelOnlyWhileScheduled() {
        var maintenance = scheduledMaintenance();
        maintenance.start();
        assertThrows(IllegalStateException.class, () -> maintenance.cancel());
    }

    @Test
    void cannotReportBreakdownOnCompletedMaintenance() {
        var maintenance = scheduledMaintenance();
        maintenance.start();
        maintenance.complete(null);
        assertThrows(IllegalStateException.class,
                () -> maintenance.reportBreakdown(BreakdownReport.report("Noise", BreakdownSeverity.LOW)));
    }

    @Test
    void breakdownSchedulesCorrectiveMaintenance() {
        var report = BreakdownReport.report("Engine does not start", BreakdownSeverity.CRITICAL);
        var maintenance = Maintenance.scheduleCorrective(1L, report, NOW);

        assertEquals(MaintenanceType.CORRECTIVE, maintenance.getType());
        assertEquals(MaintenanceStatus.SCHEDULED, maintenance.getStatus());
        assertTrue(maintenance.hasCriticalBreakdown());
    }
}
