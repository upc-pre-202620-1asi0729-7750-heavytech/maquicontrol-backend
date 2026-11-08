package com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects;

/**
 * Value object representing a cancellation or rejection reason.
 */
public record Reason(String value) {

    public Reason {
        if (value != null && value.trim().length() > 500) {
            throw new IllegalArgumentException("reason cannot exceed 500 characters");
        }
        value = (value == null || value.isBlank()) ? null : value.trim();
    }
}
