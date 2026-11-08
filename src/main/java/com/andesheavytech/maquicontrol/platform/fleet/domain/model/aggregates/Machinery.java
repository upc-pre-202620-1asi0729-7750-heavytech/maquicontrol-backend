package com.andesheavytech.maquicontrol.platform.fleet.domain.model.aggregates;

import com.andesheavytech.maquicontrol.platform.fleet.domain.model.valueobjects.MachineryLocation;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.valueobjects.MachinerySpecification;
import com.andesheavytech.maquicontrol.platform.fleet.domain.model.valueobjects.MachineryStatus;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Machinery aggregate root: a machine that its owner publishes for rent.
 */
@Getter
public class Machinery extends AbstractDomainAggregateRoot<Machinery> {

    private final Long id;
    private final Long ownerProfileId;
    private Long categoryId;
    private String name;
    private String description;
    private MachinerySpecification specification;
    private Money hourlyRate;
    private MachineryLocation location;
    private MachineryStatus status;

    private Machinery(Long id, Long ownerProfileId, Long categoryId, String name, String description,
                      MachinerySpecification specification, Money hourlyRate, MachineryLocation location,
                      MachineryStatus status) {
        if (ownerProfileId == null || ownerProfileId <= 0) {
            throw new IllegalArgumentException("ownerProfileId is required");
        }
        this.id = id;
        this.ownerProfileId = ownerProfileId;
        this.status = Objects.requireNonNull(status, "status is required");
        apply(categoryId, name, description, specification, hourlyRate, location);
    }

    /**
     * Publishes a new machine. It starts as {@link MachineryStatus#AVAILABLE}.
     */
    public static Machinery publish(Long ownerProfileId, Long categoryId, String name, String description,
                                    MachinerySpecification specification, Money hourlyRate, MachineryLocation location) {
        return new Machinery(null, ownerProfileId, categoryId, name, description, specification, hourlyRate,
                location, MachineryStatus.AVAILABLE);
    }

    public static Machinery reconstitute(Long id, Long ownerProfileId, Long categoryId, String name, String description,
                                         MachinerySpecification specification, Money hourlyRate,
                                         MachineryLocation location, MachineryStatus status) {
        return new Machinery(id, ownerProfileId, categoryId, name, description, specification, hourlyRate, location, status);
    }

    public void update(Long categoryId, String name, String description, MachinerySpecification specification,
                       Money hourlyRate, MachineryLocation location) {
        apply(categoryId, name, description, specification, hourlyRate, location);
    }

    /**
     * Status change requested by the owner.
     *
     * @throws IllegalArgumentException when the status can only be set by a rental
     * @throws IllegalStateException    when the machine is committed to a rental
     */
    public void changeStatus(MachineryStatus newStatus) {
        Objects.requireNonNull(newStatus, "status is required");
        if (!newStatus.isManuallyAssignable()) {
            throw new IllegalArgumentException("status %s is set by rentals, not by hand".formatted(newStatus));
        }
        if (status.isCommitted()) {
            throw new IllegalStateException("machinery is %s and cannot change status by hand".formatted(status));
        }
        this.status = newStatus;
    }

    /** A confirmed rental reserves the machine. */
    public void reserve() {
        if (status == MachineryStatus.AVAILABLE) {
            this.status = MachineryStatus.RESERVED;
        }
    }

    /** A rental that starts puts the machine to work. */
    public void markAsRented() {
        if (status == MachineryStatus.AVAILABLE || status == MachineryStatus.RESERVED) {
            this.status = MachineryStatus.RENTED;
        }
    }

    /** The machine goes back to the catalog when no rental keeps it committed. */
    public void release() {
        if (status.isCommitted()) {
            this.status = MachineryStatus.AVAILABLE;
        }
    }

    public boolean isOwnedBy(Long profileId) {
        return ownerProfileId.equals(profileId);
    }

    public boolean canBeDeleted() {
        return !status.isCommitted();
    }

    /**
     * Whether new rental requests can be made. Maintenance and out of service block them; reserved
     * machines can still take requests for other dates, which Rental checks.
     */
    public boolean acceptsRentalRequests() {
        return status != MachineryStatus.IN_MAINTENANCE && status != MachineryStatus.OUT_OF_SERVICE;
    }

    private void apply(Long categoryId, String name, String description, MachinerySpecification specification,
                       Money hourlyRate, MachineryLocation location) {
        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException("categoryId is required");
        }
        if (name == null || name.isBlank() || name.trim().length() > 120) {
            throw new IllegalArgumentException("name is required and must have at most 120 characters");
        }
        if (description != null && description.length() > 1000) {
            throw new IllegalArgumentException("description must have at most 1000 characters");
        }
        Objects.requireNonNull(hourlyRate, "hourlyRate is required");
        if (hourlyRate.amount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("hourlyRate must be greater than zero");
        }
        this.categoryId = categoryId;
        this.name = name.trim();
        this.description = description == null ? "" : description.trim();
        this.specification = Objects.requireNonNull(specification, "specification is required");
        this.hourlyRate = hourlyRate;
        this.location = Objects.requireNonNull(location, "location is required");
    }
}
