package com.andesheavytech.maquicontrol.platform.fleet.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.fleet.application.FleetService;
import com.andesheavytech.maquicontrol.platform.fleet.domain.MachineryStatus;
import com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal; import java.util.List;

@RestController @RequestMapping("/api/v1")
public class FleetController {
    private final FleetService fleet; public FleetController(FleetService fleet){this.fleet=fleet;}
    @PostMapping("/categories") @ResponseStatus(HttpStatus.CREATED) public CategoryResource createCategory(@Valid @RequestBody CategoryRequest request){return category(fleet.createCategory(request.code(),request.name()));}
    @GetMapping("/categories") public List<CategoryResource> categories(){return fleet.categories().stream().map(FleetController::category).toList();}
    @PutMapping("/categories/{id}") public CategoryResource renameCategory(@PathVariable Long id,@Valid @RequestBody CategoryRenameRequest request){return category(fleet.renameCategory(id,request.name()));}
    @DeleteMapping("/categories/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deactivateCategory(@PathVariable Long id){fleet.deactivateCategory(id);}
    @PostMapping("/machinery") @ResponseStatus(HttpStatus.CREATED) public MachineryResource createMachinery(@Valid @RequestBody MachineryRequest request){return machinery(fleet.register(request.toData()));}
    @GetMapping("/machinery") public List<MachineryResource> listMachinery(@RequestParam(required=false) Long categoryId,@RequestParam(required=false) MachineryStatus status,@RequestParam(required=false) String district){return fleet.list(categoryId,status,district).stream().map(FleetController::machinery).toList();}
    @GetMapping("/machinery/{id}") public MachineryResource machinery(@PathVariable Long id){return machinery(fleet.find(id));}
    @PutMapping("/machinery/{id}") public MachineryResource updateMachinery(@PathVariable Long id,@Valid @RequestBody MachineryRequest request){return machinery(fleet.update(id,request.toData()));}
    @PatchMapping("/machinery/{id}/status") public MachineryResource updateStatus(@PathVariable Long id,@Valid @RequestBody StatusRequest request){return machinery(fleet.changeStatus(id,request.status()));}
    private static CategoryResource category(CategoryPersistenceEntity c){return new CategoryResource(c.getId(),c.getCode(),c.getName(),c.isActive());}
    public static MachineryResource machinery(MachineryPersistenceEntity m){return new MachineryResource(m.getId(),m.getOwnerProfileId(),m.getCategory().getId(),m.getCategory().getName(),m.getName(),m.getDescription(),m.getBrand(),m.getModel(),m.getManufactureYear(),m.getHourlyRateAmount(),m.getHourlyRateCurrency(),m.getStatus(),m.getDepartment(),m.getProvince(),m.getDistrict(),m.getAddress(),m.getLatitude(),m.getLongitude());}
    public record CategoryRequest(@NotBlank @Size(max=40) String code,@NotBlank @Size(max=80) String name){} public record CategoryRenameRequest(@NotBlank @Size(max=80) String name){} public record CategoryResource(Long id,String code,String name,boolean active){}
    public record MachineryRequest(@NotNull @Positive Long ownerProfileId,@NotNull @Positive Long categoryId,@NotBlank @Size(max=120) String name,@Size(max=500) String description,@NotBlank @Size(max=80) String brand,@NotBlank @Size(max=80) String model,@NotNull @Min(1950) Integer manufactureYear,@NotNull @DecimalMin("0.00") BigDecimal hourlyRateAmount,@NotBlank @Pattern(regexp="(?i)PEN|USD") String hourlyRateCurrency,@NotBlank @Size(max=80) String department,@NotBlank @Size(max=80) String province,@NotBlank @Size(max=80) String district,@NotBlank @Size(max=200) String address,@DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,@DecimalMin("-180.0") @DecimalMax("180.0") Double longitude){ FleetService.MachineryData toData(){return new FleetService.MachineryData(ownerProfileId,categoryId,name,description,brand,model,manufactureYear,hourlyRateAmount,hourlyRateCurrency,department,province,district,address,latitude,longitude);} }
    public record StatusRequest(@NotNull MachineryStatus status){} public record MachineryResource(Long id,Long ownerProfileId,Long categoryId,String categoryName,String name,String description,String brand,String model,int manufactureYear,BigDecimal hourlyRateAmount,String hourlyRateCurrency,MachineryStatus status,String department,String province,String district,String address,Double latitude,Double longitude){}
}
