package com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Email address used as the login identifier of a user account.
 *
 * @param value the normalized (trimmed, lower-case) address
 */
public record EmailAddress(String value) {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    public EmailAddress {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("email is required");
        }
        value = value.trim().toLowerCase(Locale.ROOT);
        if (value.length() > 254 || !EMAIL_PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("email is not a valid email address");
        }
    }
}
