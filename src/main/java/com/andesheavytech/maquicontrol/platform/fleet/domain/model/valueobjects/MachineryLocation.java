package com.andesheavytech.maquicontrol.platform.fleet.domain.model.valueobjects;

/**
 * Where the machine is kept, using Peru's department / province / district division.
 */
public record MachineryLocation(String department, String province, String district, String address,
                                Double latitude, Double longitude) {

    public MachineryLocation {
        department = requireText(department, "department", 60);
        province = requireText(province, "province", 60);
        district = requireText(district, "district", 60);
        address = requireText(address, "address", 200);
        if (latitude != null && (latitude < -90 || latitude > 90)) {
            throw new IllegalArgumentException("latitude must be between -90 and 90");
        }
        if (longitude != null && (longitude < -180 || longitude > 180)) {
            throw new IllegalArgumentException("longitude must be between -180 and 180");
        }
    }

    private static String requireText(String value, String field, int max) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("location %s is required".formatted(field));
        }
        var trimmed = value.trim();
        if (trimmed.length() > max) {
            throw new IllegalArgumentException("location %s must have at most %d characters".formatted(field, max));
        }
        return trimmed;
    }
}
