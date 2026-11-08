package com.andesheavytech.maquicontrol.platform.fleet.domain.model.valueobjects;

import java.time.Year;

/**
 * Brand, model and manufacture year of a machine.
 */
public record MachinerySpecification(String brand, String model, Integer manufactureYear) {

    public static final int MIN_YEAR = 1950;

    public MachinerySpecification {
        if (brand == null || brand.isBlank() || brand.trim().length() > 60) {
            throw new IllegalArgumentException("brand is required and must have at most 60 characters");
        }
        if (model == null || model.isBlank() || model.trim().length() > 60) {
            throw new IllegalArgumentException("model is required and must have at most 60 characters");
        }
        var maxYear = Year.now().getValue() + 1;
        if (manufactureYear == null || manufactureYear < MIN_YEAR || manufactureYear > maxYear) {
            throw new IllegalArgumentException("manufactureYear must be between %d and %d".formatted(MIN_YEAR, maxYear));
        }
        brand = brand.trim();
        model = model.trim();
    }
}
