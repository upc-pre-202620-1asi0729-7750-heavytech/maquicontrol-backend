package com.andesheavytech.maquicontrol.platform.billing.domain.model.valueobjects;

/**
 * Value object representing a penalty or rejection reason.
 */
public record Reason(String value) {

    public Reason {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("reason cannot be blank");
        }
        if (value.trim().length() > 300) {
            throw new IllegalArgumentException("reason cannot exceed 300 characters");
        }
        value = value.trim();
    }
}
