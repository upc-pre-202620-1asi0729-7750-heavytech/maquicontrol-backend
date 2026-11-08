package com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.resources;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Public view of a user account.
 */
public record UserAccountResource(Long id, String email, String status, List<String> roles, LocalDateTime lastLoginAt) {
}
