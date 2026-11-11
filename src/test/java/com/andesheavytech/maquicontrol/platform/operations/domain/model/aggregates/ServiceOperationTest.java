package com.andesheavytech.maquicontrol.platform.operations.domain.model.aggregates;

import com.andesheavytech.maquicontrol.platform.operations.domain.model.entities.WorkedHours;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.Notes;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.OperationStatus;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.WorkShift;
import com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects.WorkedHoursStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ServiceOperationTest {

    private final LocalDate TODAY = LocalDate.of(2026, 11, 20);

    @Test
    void startCreatesOperationInProgress() {
        var op = ServiceOperation.start(10L, 5L, 3L);
        assertEquals(10L, op.getRentalId());
        assertEquals(5L, op.getMachineryId());
        assertEquals(3L, op.getOperatorProfileId());
        assertEquals(OperationStatus.IN_PROGRESS, op.getStatus());
        assertTrue(op.isInProgress());
        assertNotNull(op.getStartedAt());
    }

    @Test
    void recordWorkedHoursAddsPendingRecord() {
        var op = ServiceOperation.start(10L, 5L, 3L);
        var shift = new WorkShift(TODAY, LocalTime.of(8, 0), LocalTime.of(17, 0));
        var record = WorkedHours.record(shift, new Notes("Standard shift"));

        op.recordWorkedHours(record);

        assertEquals(1, op.getWorkedHours().size());
        assertEquals(WorkedHoursStatus.PENDING, op.getWorkedHours().get(0).getStatus());
        assertEquals(new BigDecimal("9.00"), op.getWorkedHours().get(0).getTotalHours().value());
    }

    @Test
    void cannotCompleteWhileRecordsArePending() {
        var op = ServiceOperation.start(10L, 5L, 3L);
        var shift = new WorkShift(TODAY, LocalTime.of(8, 0), LocalTime.of(16, 0));
        op.recordWorkedHours(WorkedHours.record(shift, null));

        assertThrows(IllegalStateException.class, op::complete);
    }

    @Test
    void validateAndCompleteOperationCalculatesTotalHours() {
        var op = ServiceOperation.start(10L, 5L, 3L);
        var shift1 = new WorkShift(TODAY, LocalTime.of(8, 0), LocalTime.of(12, 0)); // 4 hours
        var record1 = WorkedHours.reconstitute(1L, shift1, shift1.duration(), WorkedHoursStatus.PENDING, null, null, null);
        op.recordWorkedHours(record1);

        var shift2 = new WorkShift(TODAY, LocalTime.of(13, 0), LocalTime.of(17, 0)); // 4 hours
        var record2 = WorkedHours.reconstitute(2L, shift2, shift2.duration(), WorkedHoursStatus.PENDING, null, null, null);
        op.recordWorkedHours(record2);

        op.validateWorkedHours(1L, 99L);
        op.validateWorkedHours(2L, 99L);

        assertEquals(new BigDecimal("8.00"), op.calculateTotalHours().value());

        op.complete();
        assertEquals(OperationStatus.COMPLETED, op.getStatus());
        assertNotNull(op.getCompletedAt());
        assertFalse(op.isInProgress());
    }

    @Test
    void rejectedHoursAreNotIncludedInTotalHours() {
        var op = ServiceOperation.start(10L, 5L, 3L);
        var shift1 = new WorkShift(TODAY, LocalTime.of(8, 0), LocalTime.of(16, 0)); // 8 hours
        var record1 = WorkedHours.reconstitute(1L, shift1, shift1.duration(), WorkedHoursStatus.PENDING, null, null, null);
        op.recordWorkedHours(record1);

        var shift2 = new WorkShift(TODAY.plusDays(1), LocalTime.of(8, 0), LocalTime.of(12, 0)); // 4 hours
        var record2 = WorkedHours.reconstitute(2L, shift2, shift2.duration(), WorkedHoursStatus.PENDING, null, null, null);
        op.recordWorkedHours(record2);

        op.validateWorkedHours(1L, 99L);
        op.rejectWorkedHours(2L, 99L);

        assertEquals(new BigDecimal("8.00"), op.calculateTotalHours().value());
        op.complete();
        assertEquals(OperationStatus.COMPLETED, op.getStatus());
    }

    @Test
    void shiftValidationRejectsEndTimeBeforeStartTime() {
        assertThrows(IllegalArgumentException.class, () ->
                new WorkShift(TODAY, LocalTime.of(18, 0), LocalTime.of(8, 0)));
    }
}
