package com.andesheavytech.maquicontrol.platform.rental.interfaces.rest.resources;

import java.math.BigDecimal;

public record RentalAmountResource(BigDecimal amount, String currency) {
}
