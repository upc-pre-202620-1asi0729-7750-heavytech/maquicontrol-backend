package com.andesheavytech.maquicontrol.platform.rental.domain.model.aggregates;

import com.andesheavytech.maquicontrol.platform.rental.domain.model.events.RentalCancelledEvent;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.events.RentalCompletedEvent;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.events.RentalConfirmedEvent;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.events.RentalRejectedEvent;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.events.RentalRequestedEvent;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.events.RentalStartedEvent;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects.RentalPeriod;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.valueobjects.RentalStatus;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Rental aggregate root: a contractor's request to rent a machine for some dates.
 */
@Getter
public class Rental extends AbstractDomainAggregateRoot<Rental> {

    /** Billable hours per rental day. */
    public static final int HOURS_PER_DAY = 8;

    private final Long id;
    private final Long machineryId;
    private final Long contractorProfileId;
    private final RentalPeriod period;
    private final Money totalAmount;
    private RentalStatus status;
    private String cancellationReason;
    private final LocalDateTime requestedAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;

    private Rental(Long id, Long machineryId, Long contractorProfileId, RentalPeriod period, Money totalAmount,
                   RentalStatus status, String cancellationReason, LocalDateTime requestedAt,
                   LocalDateTime confirmedAt, LocalDateTime cancelledAt) {
        if (machineryId == null || machineryId <= 0) {
            throw new IllegalArgumentException("machineryId is required");
        }
        if (contractorProfileId == null || contractorProfileId <= 0) {
            throw new IllegalArgumentException("contractorProfileId is required");
        }
        this.id = id;
        this.machineryId = machineryId;
        this.contractorProfileId = contractorProfileId;
        this.period = Objects.requireNonNull(period, "period is required");
        this.totalAmount = Objects.requireNonNull(totalAmount, "totalAmount is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.cancellationReason = cancellationReason;
        this.requestedAt = Objects.requireNonNull(requestedAt, "requestedAt is required");
        this.confirmedAt = confirmedAt;
        this.cancelledAt = cancelledAt;
    }

    /**
     * Creates a rental request. The total is the hourly rate times 8 hours per day.
     *
     * @param today the current date, so the start date is not in the past
     */
    public static Rental request(Long machineryId, Long contractorProfileId, RentalPeriod period, Money hourlyRate,
                                 LocalDate today) {
        Objects.requireNonNull(period, "period is required");
        Objects.requireNonNull(hourlyRate, "hourlyRate is required");
        if (period.startsBefore(today)) {
            throw new IllegalArgumentException("startDate cannot be in the past");
        }
        var total = hourlyRate.multiply(period.durationInDays() * HOURS_PER_DAY);
        return new Rental(null, machineryId, contractorProfileId, period, total, RentalStatus.REQUESTED, null,
                LocalDateTime.now(), null, null);
    }

    public static Rental reconstitute(Long id, Long machineryId, Long contractorProfileId, RentalPeriod period,
                                      Money totalAmount, RentalStatus status, String cancellationReason,
                                      LocalDateTime requestedAt, LocalDateTime confirmedAt, LocalDateTime cancelledAt) {
        return new Rental(id, machineryId, contractorProfileId, period, totalAmount, status, cancellationReason,
                requestedAt, confirmedAt, cancelledAt);
    }

    /**
     * Registers the {@link RentalRequestedEvent}. Called by the repository adapter once the id is assigned.
     */
    public void onRequested() {
        registerDomainEvent(new RentalRequestedEvent(id, machineryId, contractorProfileId));
    }

    /** The owner accepts the request. */
    public void confirm() {
        requireStatus(RentalStatus.REQUESTED, "confirm");
        this.status = RentalStatus.CONFIRMED;
        this.confirmedAt = LocalDateTime.now();
        registerDomainEvent(new RentalConfirmedEvent(id, machineryId, contractorProfileId));
    }

    /** The owner declines the request. */
    public void reject(String reason) {
        requireStatus(RentalStatus.REQUESTED, "reject");
        this.cancellationReason = requireReason(reason);
        this.status = RentalStatus.REJECTED;
        this.cancelledAt = LocalDateTime.now();
        registerDomainEvent(new RentalRejectedEvent(id, machineryId, contractorProfileId, cancellationReason));
    }

    /** The contractor or the owner calls the rental off. */
    public void cancel(String reason) {
        if (status.isClosed()) {
            throw new IllegalStateException("cannot cancel a rental that is %s".formatted(status));
        }
        this.cancellationReason = requireReason(reason);
        this.status = RentalStatus.CANCELLED;
        this.cancelledAt = LocalDateTime.now();
        registerDomainEvent(new RentalCancelledEvent(id, machineryId, contractorProfileId, cancellationReason));
    }

    /** Policy: a confirmed rental starts on its start date. */
    public void start() {
        requireStatus(RentalStatus.CONFIRMED, "start");
        this.status = RentalStatus.IN_PROGRESS;
        registerDomainEvent(new RentalStartedEvent(id, machineryId, contractorProfileId));
    }

    /** Policy: a rental in progress completes after its end date. */
    public void complete() {
        requireStatus(RentalStatus.IN_PROGRESS, "complete");
        this.status = RentalStatus.COMPLETED;
        registerDomainEvent(new RentalCompletedEvent(id, machineryId, contractorProfileId));
    }

    public boolean isRequestedBy(Long profileId) {
        return contractorProfileId.equals(profileId);
    }

    private void requireStatus(RentalStatus expected, String action) {
        if (status != expected) {
            throw new IllegalStateException("cannot %s a rental that is %s".formatted(action, status));
        }
    }

    private static String requireReason(String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("reason is required");
        }
        var trimmed = reason.trim();
        if (trimmed.length() > 500) {
            throw new IllegalArgumentException("reason must have at most 500 characters");
        }
        return trimmed;
    }
}
