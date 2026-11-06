package com.andesheavytech.maquicontrol.platform.profiles.domain;

/** Validated contact details required for every profile. */
public record ContactInformation(String phone, String address) {
    public ContactInformation {
        if (phone == null || !phone.trim().matches("\\+?[0-9]{7,15}")) throw new IllegalArgumentException("phone must contain 7 to 15 digits");
        if (address == null || address.isBlank() || address.trim().length() > 200) throw new IllegalArgumentException("address is required and must be at most 200 characters");
        phone = phone.trim(); address = address.trim();
    }
}
