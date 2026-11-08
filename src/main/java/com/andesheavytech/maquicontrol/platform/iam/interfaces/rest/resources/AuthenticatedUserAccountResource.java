package com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.resources;

import java.util.List;

/**
 * Response of a successful sign in.
 */
public record AuthenticatedUserAccountResource(Long id, String email, List<String> roles, String token) {
}
