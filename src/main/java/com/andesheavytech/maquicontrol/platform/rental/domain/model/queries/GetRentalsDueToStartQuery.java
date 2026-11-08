package com.andesheavytech.maquicontrol.platform.rental.domain.model.queries;

import java.time.LocalDate;

public record GetRentalsDueToStartQuery(LocalDate today) {
}
