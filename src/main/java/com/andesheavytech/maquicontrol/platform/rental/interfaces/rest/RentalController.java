package com.andesheavytech.maquicontrol.platform.rental.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.rental.application.RentalService;
import com.andesheavytech.maquicontrol.platform.rental.domain.RentalStatus;
import com.andesheavytech.maquicontrol.platform.rental.infrastructure.persistence.RentalPersistenceEntity;
import jakarta.validation.Valid; import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus; import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal; import java.time.LocalDate; import java.time.LocalDateTime; import java.util.List;

@RestController @RequestMapping("/api/v1/rentals")
public class RentalController {
    private final RentalService rentals; public RentalController(RentalService rentals){this.rentals=rentals;}
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public RentalResource request(@Valid @RequestBody RentalRequest request){return resource(rentals.request(new RentalService.Request(request.machineryId(),request.contractorProfileId(),request.startDate(),request.endDate())));}
    @GetMapping public List<RentalResource> list(@RequestParam(required=false) Long contractorProfileId,@RequestParam(required=false) Long machineryId,@RequestParam(required=false) RentalStatus status){return rentals.list(contractorProfileId,machineryId,status).stream().map(RentalController::resource).toList();}
    @GetMapping("/{id}") public RentalResource find(@PathVariable Long id){return resource(rentals.find(id));}
    @GetMapping("/availability") public AvailabilityResource availability(@RequestParam Long machineryId,@RequestParam LocalDate startDate,@RequestParam LocalDate endDate){return new AvailabilityResource(machineryId,startDate,endDate,rentals.available(machineryId,startDate,endDate));}
    @PatchMapping("/{id}/confirm") public RentalResource confirm(@PathVariable Long id){return resource(rentals.confirm(id));}
    @PatchMapping("/{id}/reject") public RentalResource reject(@PathVariable Long id,@Valid @RequestBody ReasonRequest request){return resource(rentals.reject(id,request.reason()));}
    @PatchMapping("/{id}/cancel") public RentalResource cancel(@PathVariable Long id,@Valid @RequestBody ReasonRequest request){return resource(rentals.cancel(id,request.reason()));}
    @PatchMapping("/{id}/period") public RentalResource reschedule(@PathVariable Long id,@Valid @RequestBody PeriodRequest request){return resource(rentals.reschedule(id,request.startDate(),request.endDate()));}
    private static RentalResource resource(RentalPersistenceEntity r){return new RentalResource(r.getId(),r.getMachineryId(),r.getContractorProfileId(),r.getStartDate(),r.getEndDate(),r.getStatus(),r.getTotalAmount(),r.getTotalCurrency(),r.getCancellationReason(),r.getRequestedAt(),r.getConfirmedAt(),r.getCancelledAt());}
    public record RentalRequest(@NotNull @Positive Long machineryId,@NotNull @Positive Long contractorProfileId,@NotNull LocalDate startDate,@NotNull LocalDate endDate){} public record PeriodRequest(@NotNull LocalDate startDate,@NotNull LocalDate endDate){} public record ReasonRequest(@NotBlank @Size(max=300) String reason){} public record AvailabilityResource(Long machineryId,LocalDate startDate,LocalDate endDate,boolean available){} public record RentalResource(Long id,Long machineryId,Long contractorProfileId,LocalDate startDate,LocalDate endDate,RentalStatus status,BigDecimal totalAmount,String totalCurrency,String cancellationReason,LocalDateTime requestedAt,LocalDateTime confirmedAt,LocalDateTime cancelledAt){}
}
