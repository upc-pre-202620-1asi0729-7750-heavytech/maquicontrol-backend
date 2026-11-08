package com.andesheavytech.maquicontrol.platform.iam.domain.model.events;

/**
 * Raised when a visitor creates a user account.
 *
 * @param userAccountId the new account id
 * @param email         the account email
 * @param roleName      the role chosen on sign up
 */
public record UserSignedUpEvent(Long userAccountId, String email, String roleName) {
}
