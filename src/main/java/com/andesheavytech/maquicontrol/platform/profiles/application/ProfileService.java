package com.andesheavytech.maquicontrol.platform.profiles.application;

import com.andesheavytech.maquicontrol.platform.profiles.domain.*;
import com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service @Transactional
public class ProfileService {
    private final ProfileJpaRepository profiles; private final OrganizationJpaRepository organizations;
    public ProfileService(ProfileJpaRepository profiles, OrganizationJpaRepository organizations) { this.profiles=profiles;this.organizations=organizations; }
    @Transactional(readOnly=true) public ProfilePersistenceEntity mine(Long accountId) { return profile(accountId); }
    public ProfilePersistenceEntity update(Long accountId, ProfileData data) { var contact=new ContactInformation(data.phone(),data.address()); var profile=profile(accountId); profile.update(required(data.firstName(),"firstName"),required(data.lastName(),"lastName"),contact.phone(),contact.address(),nullable(data.documentNumber())); return profile; }
    public OrganizationPersistenceEntity registerOrganization(Long accountId, OrganizationData data) {
        var profile=profile(accountId); var taxId=taxId(data.taxId()); if(organizations.existsByTaxId(taxId)) throw conflict("An organization with this taxId already exists");
        var organization=organizations.save(new OrganizationPersistenceEntity(required(data.name(),"name"),requiredType(data.type()),taxId,nullable(data.email()),nullable(data.phone()),nullable(data.address()))); profile.join(organization); return organization;
    }
    public OrganizationPersistenceEntity updateOrganization(Long accountId, Long organizationId, OrganizationData data) {
        var profile=profile(accountId); if(profile.getOrganization()==null || !profile.getOrganization().getId().equals(organizationId)) throw forbidden("You can only update your own organization");
        var organization=profile.getOrganization(); var taxId=taxId(data.taxId()); if(!organization.getTaxId().equals(taxId) && organizations.existsByTaxId(taxId)) throw conflict("An organization with this taxId already exists");
        organization.update(required(data.name(),"name"),requiredType(data.type()),taxId,nullable(data.email()),nullable(data.phone()),nullable(data.address())); return organization;
    }
    private ProfilePersistenceEntity profile(Long accountId) { return profiles.findByUserAccountId(accountId).orElseThrow(() -> notFound("Profile not found")); }
    private static OrganizationType requiredType(OrganizationType value) { if(value==null) throw bad("organization type is required"); return value; }
    private static String taxId(String value) { if(value==null || !value.trim().matches("[0-9]{8,11}")) throw bad("taxId must contain 8 to 11 digits"); return value.trim(); }
    private static String required(String value,String field) { if(value==null||value.isBlank()) throw bad(field+" is required"); return value.trim(); }
    private static String nullable(String value) { return value==null?null:value.trim(); }
    private static ResponseStatusException bad(String message) { return new ResponseStatusException(HttpStatus.BAD_REQUEST,message); }
    private static ResponseStatusException conflict(String message) { return new ResponseStatusException(HttpStatus.CONFLICT,message); }
    private static ResponseStatusException forbidden(String message) { return new ResponseStatusException(HttpStatus.FORBIDDEN,message); }
    private static ResponseStatusException notFound(String message) { return new ResponseStatusException(HttpStatus.NOT_FOUND,message); }
    public record ProfileData(String firstName,String lastName,String phone,String address,String documentNumber) { }
    public record OrganizationData(String name,OrganizationType type,String taxId,String email,String phone,String address) { }
}
