package com.andesheavytech.maquicontrol.platform.fleet.domain;

import java.util.Objects;

/** Location information required to search machinery near a construction site. */
public record MachineryLocation(String department, String province, String district, String address,
                                Double latitude, Double longitude) {
    public MachineryLocation {
        department = required(department, "department");
        province = required(province, "province");
        district = required(district, "district");
        address = required(address, "address");
        if (latitude != null && (latitude < -90 || latitude > 90)) throw new IllegalArgumentException("latitude must be between -90 and 90");
        if (longitude != null && (longitude < -180 || longitude > 180)) throw new IllegalArgumentException("longitude must be between -180 and 180");
    }
    private static String required(String value, String name) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(name + " is required");
        return value.trim();
    }
}
