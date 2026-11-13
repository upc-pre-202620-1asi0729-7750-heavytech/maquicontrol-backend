package com.andesheavytech.maquicontrol.platform.rental.application.queryservices;

import com.andesheavytech.maquicontrol.platform.rental.domain.model.aggregates.Rental;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.CheckMachineryAvailabilityQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalByIdQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalsDueToCompleteQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.GetRentalsDueToStartQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.ViewFleetRentalsQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.ViewMyReservationsQuery;
import com.andesheavytech.maquicontrol.platform.rental.domain.model.queries.ViewPendingRequestsQuery;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;

import java.util.List;
import java.util.Optional;

/**
 * Query service for the Rental Management bounded context.
 */
public interface RentalManagementQueryService {

    Optional<Rental> handle(GetRentalByIdQuery query);

    List<Rental> handle(ViewMyReservationsQuery query);

    List<Rental> handle(ViewPendingRequestsQuery query);

    List<Rental> handle(ViewFleetRentalsQuery query);

    Result<Boolean, ApplicationError> handle(CheckMachineryAvailabilityQuery query);

    List<Rental> handle(GetRentalsDueToStartQuery query);

    List<Rental> handle(GetRentalsDueToCompleteQuery query);
}
