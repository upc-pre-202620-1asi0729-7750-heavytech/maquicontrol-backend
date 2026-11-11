package com.andesheavytech.maquicontrol.platform.profiles.domain.model.events;

/**
 * Raised when a user account completes its profile.
 */
public record ProfileCreatedEvent(Long profileId, Long userAccountId) {
}
