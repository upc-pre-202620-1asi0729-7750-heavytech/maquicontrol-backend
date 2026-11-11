package com.andesheavytech.maquicontrol.platform.profiles.application.internal.commandservices;

import com.andesheavytech.maquicontrol.platform.profiles.application.commandservices.ProfilesManagementCommandService;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.aggregates.Profile;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.commands.CreateOrganizationCommand;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.commands.CreateProfileCommand;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.commands.UpdateOrganizationCommand;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.commands.UpdateProfileCommand;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.entities.Organization;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.ContactInformation;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.DocumentNumber;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.PersonName;
import com.andesheavytech.maquicontrol.platform.profiles.domain.model.valueobjects.TaxId;
import com.andesheavytech.maquicontrol.platform.profiles.domain.repositories.ProfileRepository;
import com.andesheavytech.maquicontrol.platform.shared.application.result.ApplicationError;
import com.andesheavytech.maquicontrol.platform.shared.application.result.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of {@link ProfilesManagementCommandService}.
 */
@Service
public class ProfilesManagementCommandServiceImpl implements ProfilesManagementCommandService {

    private final ProfileRepository profileRepository;

    public ProfilesManagementCommandServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public Result<Profile, ApplicationError> handle(CreateProfileCommand command) {
        if (profileRepository.existsByUserAccountId(command.userAccountId())) {
            return Result.failure(ApplicationError.conflict("profile", "the signed-in account already has a profile"));
        }
        try {
            var documentNumber = new DocumentNumber(command.documentType(), command.documentNumber());
            if (profileRepository.existsByDocumentNumber(documentNumber.value(), null)) {
                return Result.failure(ApplicationError.conflict("profile", "document number is already registered"));
            }
            var profile = Profile.create(command.userAccountId(),
                    new PersonName(command.firstName(), command.lastName()),
                    documentNumber,
                    new ContactInformation(command.phoneNumber(), command.secondaryEmail(), command.address(),
                            command.district(), command.city()));
            return Result.success(profileRepository.save(profile));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Result.failure(ApplicationError.validationError("profile", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Profile, ApplicationError> handle(UpdateProfileCommand command) {
        var found = profileRepository.findById(command.profileId());
        if (found.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Profile", String.valueOf(command.profileId())));
        }
        var profile = found.get();
        if (!profile.belongsTo(command.userAccountId())) {
            return Result.failure(ApplicationError.forbidden("profile", "only the owner can update this profile"));
        }
        try {
            var documentNumber = new DocumentNumber(command.documentType(), command.documentNumber());
            if (profileRepository.existsByDocumentNumber(documentNumber.value(), profile.getId())) {
                return Result.failure(ApplicationError.conflict("profile", "document number is already registered"));
            }
            profile.update(new PersonName(command.firstName(), command.lastName()), documentNumber,
                    new ContactInformation(command.phoneNumber(), command.secondaryEmail(), command.address(),
                            command.district(), command.city()));
            return Result.success(profileRepository.save(profile));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Result.failure(ApplicationError.validationError("profile", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Profile, ApplicationError> handle(CreateOrganizationCommand command) {
        var found = profileRepository.findByUserAccountId(command.userAccountId());
        if (found.isEmpty()) {
            return Result.failure(ApplicationError.businessRuleViolation("profile required",
                    "create your profile before registering an organization"));
        }
        var profile = found.get();
        if (profile.organization().isPresent()) {
            return Result.failure(ApplicationError.conflict("organization", "the profile already belongs to an organization"));
        }
        try {
            var taxId = new TaxId(command.taxId());
            if (profileRepository.existsOrganizationByTaxId(taxId.value(), null)) {
                return Result.failure(ApplicationError.conflict("organization", "taxId is already registered"));
            }
            profile.registerOrganization(new Organization(command.legalName(), command.tradeName(), taxId,
                    command.organizationType(), command.address()));
            return Result.success(profileRepository.save(profile));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Result.failure(ApplicationError.validationError("organization", e.getMessage()));
        }
    }

    @Override
    @Transactional
    public Result<Profile, ApplicationError> handle(UpdateOrganizationCommand command) {
        var found = profileRepository.findByOrganizationId(command.organizationId());
        if (found.isEmpty()) {
            return Result.failure(ApplicationError.notFound("Organization", String.valueOf(command.organizationId())));
        }
        var profile = found.get();
        if (!profile.belongsTo(command.userAccountId())) {
            return Result.failure(ApplicationError.forbidden("organization", "only a member can update this organization"));
        }
        try {
            var taxId = new TaxId(command.taxId());
            if (profileRepository.existsOrganizationByTaxId(taxId.value(), command.organizationId())) {
                return Result.failure(ApplicationError.conflict("organization", "taxId is already registered"));
            }
            profile.getOrganization().update(command.legalName(), command.tradeName(), taxId,
                    command.organizationType(), command.address());
            return Result.success(profileRepository.save(profile));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Result.failure(ApplicationError.validationError("organization", e.getMessage()));
        }
    }
}
