package com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects;

import java.util.regex.Pattern;

/**
 * Contact information of a profile. Every field is optional, but each one is validated when present.
 */
public record ContactInformation(String phoneNumber, String secondaryEmail, String address, String district, String city) {

    private static final Pattern PHONE_PATTERN = Pattern.compile("\\+?[0-9]{7,15}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public ContactInformation {
        phoneNumber = blankToNull(phoneNumber);
        secondaryEmail = blankToNull(secondaryEmail);
        address = blankToNull(address);
        district = blankToNull(district);
        city = blankToNull(city);
        if (phoneNumber != null && !PHONE_PATTERN.matcher(phoneNumber).matches()) {
            throw new IllegalArgumentException("phoneNumber must have 7 to 15 digits");
        }
        if (secondaryEmail != null && !EMAIL_PATTERN.matcher(secondaryEmail).matches()) {
            throw new IllegalArgumentException("secondaryEmail is not a valid email address");
        }
        requireMaxLength(address, "address", 200);
        requireMaxLength(district, "district", 80);
        requireMaxLength(city, "city", 80);
    }

    public static ContactInformation empty() {
        return new ContactInformation(null, null, null, null, null);
    }

    private static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static void requireMaxLength(String value, String field, int max) {
        if (value != null && value.length() > max) {
            throw new IllegalArgumentException("%s must have at most %d characters".formatted(field, max));
        }
    }
}
