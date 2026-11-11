package com.andesheavytech.maquicontrol.platform.subscription.domain.model.commands;

import com.andesheavytech.maquicontrol.platform.subscription.domain.model.valueobjects.PlanCode;

public record SubscribeToPlanCommand(Long ownerProfileId, PlanCode planCode) {
}
