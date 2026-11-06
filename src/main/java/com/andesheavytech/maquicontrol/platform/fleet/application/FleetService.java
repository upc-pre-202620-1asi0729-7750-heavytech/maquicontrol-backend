package com.andesheavytech.maquicontrol.platform.fleet.application;

import com.andesheavytech.maquicontrol.platform.fleet.domain.MachineryLocation;
import com.andesheavytech.maquicontrol.platform.fleet.domain.MachineryStatus;
import com.andesheavytech.maquicontrol.platform.fleet.infrastructure.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;
import java.time.Year;
import java.util.List;

/** Command and query application service for the Fleet bounded context. */
@Service @Transactional
public class FleetService {
    private final CategoryJpaRepository categories; private final MachineryJpaRepository machinery;
    public FleetService(CategoryJpaRepository categories, MachineryJpaRepository machinery){this.categories=categories;this.machinery=machinery;}
    public CategoryPersistenceEntity createCategory(String code, String name){
        code=required(code,"code").toUpperCase(); name=required(name,"name");
        if(categories.findByCodeIgnoreCase(code).isPresent()) throw conflict("A category with this code already exists");
        return categories.save(new CategoryPersistenceEntity(code,name));
    }
    public CategoryPersistenceEntity renameCategory(Long id,String name){ var c=category(id); c.rename(required(name,"name")); return c; }
    public void deactivateCategory(Long id){ var c=category(id); if(!machinery.findAllByCategoryId(id).isEmpty()) throw conflict("A category assigned to machinery cannot be deactivated"); c.deactivate(); }
    @Transactional(readOnly=true) public List<CategoryPersistenceEntity> categories(){return categories.findAllByActiveTrueOrderByNameAsc();}
    public MachineryPersistenceEntity register(MachineryData data){ return machinery.save(new MachineryPersistenceEntity(requiredId(data.ownerProfileId(),"ownerProfileId"), activeCategory(data.categoryId()), required(data.name(),"name"), nullable(data.description()), required(data.brand(),"brand"), required(data.model(),"model"), validYear(data.manufactureYear()), validRate(data.hourlyRateAmount()), currency(data.hourlyRateCurrency()), location(data).department(), location(data).province(), location(data).district(), location(data).address(), location(data).latitude(), location(data).longitude())); }
    public MachineryPersistenceEntity update(Long id, MachineryData data){ var m=find(id); var l=location(data); m.update(activeCategory(data.categoryId()),required(data.name(),"name"),nullable(data.description()),required(data.brand(),"brand"),required(data.model(),"model"),validYear(data.manufactureYear()),validRate(data.hourlyRateAmount()),currency(data.hourlyRateCurrency()),l.department(),l.province(),l.district(),l.address(),l.latitude(),l.longitude()); return m; }
    public MachineryPersistenceEntity changeStatus(Long id, MachineryStatus status){ if(status==null) throw bad("status is required");var m=find(id);m.changeStatus(status);return m; }
    @Transactional(readOnly=true) public MachineryPersistenceEntity find(Long id){return machinery.findById(id).orElseThrow(()->notFound("Machinery",id));}
    @Transactional(readOnly=true) public List<MachineryPersistenceEntity> list(Long categoryId,MachineryStatus status,String district){ if(categoryId!=null)return machinery.findAllByCategoryId(categoryId); if(status!=null)return machinery.findAllByStatus(status);if(district!=null&&!district.isBlank())return machinery.findAllByDistrictContainingIgnoreCase(district.trim());return machinery.findAll(); }
    private CategoryPersistenceEntity activeCategory(Long id){var c=category(requiredId(id,"categoryId"));if(!c.isActive())throw conflict("The selected category is inactive");return c;}
    private CategoryPersistenceEntity category(Long id){return categories.findById(id).orElseThrow(()->notFound("Category",id));}
    private static MachineryLocation location(MachineryData d){return new MachineryLocation(d.department(),d.province(),d.district(),d.address(),d.latitude(),d.longitude());}
    private static Long requiredId(Long value,String field){if(value==null||value<=0)throw bad(field+" must be positive");return value;}
    private static String required(String value,String field){if(value==null||value.isBlank())throw bad(field+" is required");return value.trim();}
    private static String nullable(String value){return value==null?null:value.trim();}
    private static int validYear(Integer value){if(value==null||value<1950||value>Year.now().getValue()+1)throw bad("manufactureYear is invalid");return value;}
    private static BigDecimal validRate(BigDecimal value){if(value==null||value.signum()<0)throw bad("hourlyRateAmount must be non-negative");return value;}
    private static String currency(String value){if(value==null||!(value.equalsIgnoreCase("PEN")||value.equalsIgnoreCase("USD")))throw bad("hourlyRateCurrency must be PEN or USD");return value.toUpperCase();}
    private static ResponseStatusException bad(String detail){return new ResponseStatusException(HttpStatus.BAD_REQUEST,detail);} private static ResponseStatusException conflict(String detail){return new ResponseStatusException(HttpStatus.CONFLICT,detail);} private static ResponseStatusException notFound(String type,Long id){return new ResponseStatusException(HttpStatus.NOT_FOUND,type+" not found: "+id);}
    public record MachineryData(Long ownerProfileId,Long categoryId,String name,String description,String brand,String model,Integer manufactureYear,BigDecimal hourlyRateAmount,String hourlyRateCurrency,String department,String province,String district,String address,Double latitude,Double longitude){}
}
