package com.andesheavytech.maquicontrol.platform.billing.domain.model.commands;

import com.andesheavytech.maquicontrol.platform.shared.domain.model.valueobjects.Money;

public record CalculateRentalAmountCommand(Long rentalId, Money amount) {
}
