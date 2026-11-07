package com.andesheavytech.maquicontrol.platform.profiles.interfaces.rest;

import com.andesheavytech.maquicontrol.platform.profiles.application.ProfileService;
import com.andesheavytech.maquicontrol.platform.profiles.domain.OrganizationType;
import com.andesheavytech.maquicontrol.platform.profiles.infrastructure.persistence.*;
import jakarta.validation.Valid; import jakarta.validation.constraints.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal; import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1")
public class ProfileController {
    private final ProfileService profiles;
    public ProfileController(ProfileService profiles) { this.profiles=profiles; }
    @GetMapping("/profiles/me") public ProfileResource mine(@AuthenticationPrincipal Long accountId) { return profile(profiles.mine(accountId)); }
    @PutMapping("/profiles/me") public ProfileResource update(@AuthenticationPrincipal Long accountId,@Valid @RequestBody ProfileRequest request) { return profile(profiles.update(accountId,request.data())); }
    @PostMapping("/organizations") public OrganizationResource createOrganization(@AuthenticationPrincipal Long accountId,@Valid @RequestBody OrganizationRequest request) { return organization(profiles.registerOrganization(accountId,request.data())); }
    @PutMapping("/organizations/{id}") public OrganizationResource updateOrganization(@AuthenticationPrincipal Long accountId,@PathVariable Long id,@Valid @RequestBody OrganizationRequest request) { return organization(profiles.updateOrganization(accountId,id,request.data())); }
    private static ProfileResource profile(ProfilePersistenceEntity p) { return new ProfileResource(p.getId(),p.getUserAccountId(),p.getFirstName(),p.getLastName(),p.getPhone(),p.getAddress(),p.getDocumentNumber(),p.getOrganization()==null?null:organization(p.getOrganization())); }
    private static OrganizationResource organization(OrganizationPersistenceEntity o) { return new OrganizationResource(o.getId(),o.getName(),o.getType(),o.getTaxId(),o.getEmail(),o.getPhone(),o.getAddress()); }
    public record ProfileRequest(@NotBlank @Size(max=80) String firstName,@NotBlank @Size(max=80) String lastName,@NotBlank @Pattern(regexp="\\+?[0-9]{7,15}") String phone,@NotBlank @Size(max=200) String address,@Size(max=30) String documentNumber) { ProfileService.ProfileData data(){return new ProfileService.ProfileData(firstName,lastName,phone,address,documentNumber);} }
    public record OrganizationRequest(@NotBlank @Size(max=140) String name,@NotNull OrganizationType type,@NotBlank @Pattern(regexp="[0-9]{8,11}") String taxId,@Email @Size(max=160) String email,@Size(max=30) String phone,@Size(max=200) String address) { ProfileService.OrganizationData data(){return new ProfileService.OrganizationData(name,type,taxId,email,phone,address);} }
    public record ProfileResource(Long id,Long userAccountId,String firstName,String lastName,String phone,String address,String documentNumber,OrganizationResource organization) { }
    public record OrganizationResource(Long id,String name,OrganizationType type,String taxId,String email,String phone,String address) { }
}
