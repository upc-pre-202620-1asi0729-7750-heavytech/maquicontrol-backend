package com.andesheavytech.maquicontrol.platform.operations.domain.model.valueobjects;

/**
 * Value object representing observations or notes with a length limit of 500 characters.
 */
public record Notes(String value) {

    public Notes {
        if (value != null && value.trim().length() > 500) {
            throw new IllegalArgumentException("notes cannot exceed 500 characters");
        }
        value = (value == null || value.isBlank()) ? null : value.trim();
    }
}
