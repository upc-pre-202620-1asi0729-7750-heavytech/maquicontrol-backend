package com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.transform;

import com.andesheavytech.maquicontrol.platform.iam.domain.model.commands.SignUpCommand;
import com.andesheavytech.maquicontrol.platform.iam.domain.model.valueobjects.RoleName;
import com.andesheavytech.maquicontrol.platform.iam.interfaces.rest.resources.SignUpResource;

import java.util.Arrays;
import java.util.Locale;

public final class SignUpCommandFromResourceAssembler {

    private SignUpCommandFromResourceAssembler() {
    }

    public static SignUpCommand toCommandFromResource(SignUpResource resource) {
        var normalized = resource.roleName().trim().toUpperCase(Locale.ROOT);
        var roleName = Arrays.stream(RoleName.values())
                .filter(name -> name.name().equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("roleName %s is not a valid role".formatted(resource.roleName())));
        return new SignUpCommand(resource.email(), resource.password(), roleName);
    }
}
