package com.andesheavytech.maquicontrol.platform.iam.application.commandservices;

import com.andesheavytech.maquicontrol.platform.iam.domain.model.aggregates.UserAccount;

/**
 * Result of a successful sign in: the account and the bearer token issued for it.
 *
 * @param userAccount the signed-in account
 * @param token       the issued JWT
 */
public record AuthenticatedUserAccount(UserAccount userAccount, String token) {
}
